package com.epam.talks.github.model

import com.epam.talks.github.GithubRepository
import org.json.JSONArray
import java.util.ArrayList

fun JSONArray.toRepos(): ArrayList<GithubRepository> {
	val jsonArray = this
	val repos = ArrayList<GithubRepository>(jsonArray.length())
	for (i in 0..(jsonArray.length() - 1)) {
		val item = jsonArray.getJSONObject(i)
		with(item) {
			repos.add(GithubRepository(getInt("id"),
					getString("name"),
					getString("full_name")
			)
			)
		}
	}
	return repos
}