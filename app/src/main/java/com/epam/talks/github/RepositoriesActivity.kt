package com.epam.talks.github

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.epam.talks.github.model.ApiClient
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_repositories.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
class RepositoriesActivity : AppCompatActivity(), CoroutineScope {

	override val coroutineContext: CoroutineContext
		get() = Dispatchers.Main

	private lateinit var searchFlow: MutableStateFlow<String?>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_repositories)

		searchFlow = MutableStateFlow(null)

		val reposNames = intent.extras.getStringArrayList("repos")
		repos.adapter = ReposAdapter(ArrayList(reposNames), this@RepositoriesActivity)

		val apiClient = ApiClient.ApiClientImpl(coroutineContext)

		launch {
			searchFlow.collect {
				try {
					it?.let {
						val foundRepositories = apiClient.searchRepositories(it).await()
						repos.adapter = ReposAdapter(
								foundRepositories.map { it.full_name },
								this@RepositoriesActivity)
					}
				} catch (e: Exception) {
					Toast.makeText(this@RepositoriesActivity, "Failed to search", Toast.LENGTH_SHORT)
				}
			}
		}

		searchQuery.addTextChangedListener(object: TextWatcher {
			override fun afterTextChanged(s: Editable?) {
				searchFlow.value = s.toString()
			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
		})
	}

	class RepoViewHolder(view : View)  : RecyclerView.ViewHolder(view) {

		val name : TextView = view.findViewById(R.id.repoName)
		val stars : TextView = view.findViewById(R.id.repoStars)
	}

	class ReposAdapter(val reposNames: List<String>, val context: Context) : RecyclerView.Adapter<RepoViewHolder>() {

		override fun getItemCount(): Int {
			return reposNames.count()
		}

		override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RepoViewHolder {
			val view = LayoutInflater.from(context).inflate(R.layout.repo_item, null)
			return RepoViewHolder(view)
		}

		override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
			holder.name.text = reposNames[position]
		}
	}
}

fun showRepositories(context: Context, repos : List<String>) {
	val intent = Intent(context, RepositoriesActivity::class.java)
	intent.putExtra("repos", ArrayList(repos))
	context.startActivity(intent)
}
