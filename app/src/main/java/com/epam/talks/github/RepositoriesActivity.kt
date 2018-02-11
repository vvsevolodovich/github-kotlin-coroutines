package com.epam.talks.github

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_repositories.*

class RepositoriesActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_repositories)

		val reposNames = intent.extras.getStringArrayList("repos")
		repos.adapter = object : RecyclerView.Adapter<RepoViewHolder>() {

			override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RepoViewHolder {
				val view = LayoutInflater.from(this@RepositoriesActivity).inflate(R.layout.repo_item, null)
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

	class RepoViewHolder(view : View)  : RecyclerView.ViewHolder(view) {

		val name : TextView = view.findViewById(R.id.repoName)
		val stars : TextView = view.findViewById(R.id.repoStars)
	}
}

fun showRepositories(context: Context, repos : List<String>) {
	val intent = Intent(context, RepositoriesActivity::class.java)
	intent.putExtra("repos", ArrayList(repos))
	context.startActivity(intent)
}
