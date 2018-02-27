package com.epam.talks.github

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.epam.talks.github.model.ApiClient
import com.epam.talks.github.model.ApiClientRx
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_repositories.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.experimental.channels.consume
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.reactive.publish
import java.util.concurrent.TimeUnit


class RepositoriesActivity : AppCompatActivity() {

	var publishSubject: PublishSubject<String> = PublishSubject.create()
	val broadcast = ConflatedBroadcastChannel<String>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_repositories)

		val reposNames = intent.extras.getStringArrayList("repos")
		repos.adapter = ReposAdapter(ArrayList(reposNames), this@RepositoriesActivity)

		val apiClientRxImpl = ApiClientRx.ApiClientRxImpl()
		val apiClient = ApiClient.ApiClientImpl()

		launch(UI) {
			broadcast.consumeEach {
				delay(300)
				Log.d("TAG", "Query = ${it}")
				val query = it
				val foundRepositories = apiClient.searchRepositories(query).await()
				repos.adapter = ReposAdapter(
									foundRepositories.map { it.full_name },
							this@RepositoriesActivity)
			}
		}

		publishSubject
				.debounce(300, TimeUnit.MILLISECONDS)
				.distinctUntilChanged()
				.switchMap { searchQuery -> apiClientRxImpl.searchRepositories(searchQuery) }
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe({
					repos.adapter = ReposAdapter(
										it.map { it.full_name },
								this@RepositoriesActivity)
				})

		searchQuery.addTextChangedListener(object: TextWatcher {
			override fun afterTextChanged(s: Editable?) {
				//publishSubject.onNext(s.toString())
				broadcast.offer(s.toString())
			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
			}

			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
			}
		})
	}

	class RepoViewHolder(view : View)  : RecyclerView.ViewHolder(view) {

		val name : TextView = view.findViewById(R.id.repoName)
		val stars : TextView = view.findViewById(R.id.repoStars)
	}

	class ReposAdapter(val reposNames: List<String>, val context: Context) : RecyclerView.Adapter<RepoViewHolder>() {

		override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RepoViewHolder {
			val view = LayoutInflater.from(context).inflate(R.layout.repo_item, null)
			return RepoViewHolder(view)
		}

		override fun onBindViewHolder(holder: RepoViewHolder?, position: Int) {
			holder!!.name.text = reposNames[position]
		}

		override fun getItemCount(): Int {
			return reposNames.count()
		}
	}
}

fun showRepositories(context: Context, repos : List<String>) {
	val intent = Intent(context, RepositoriesActivity::class.java)
	intent.putExtra("repos", ArrayList(repos))
	context.startActivity(intent)
}
