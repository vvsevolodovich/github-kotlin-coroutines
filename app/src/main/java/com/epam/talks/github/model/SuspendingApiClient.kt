package com.epam.talks.github.model

import com.epam.talks.github.GithubRepository
import com.epam.talks.github.GithubUser
import khttp.get
import khttp.structures.authorization.Authorization

interface SuspendingApiClient {

	suspend fun login(auth: Authorization) : GithubUser
	suspend fun getRepositories(reposUrl: String, auth: Authorization) : List<GithubRepository>
	suspend fun searchRepositories(searchQuery: String) : List<GithubRepository>

	class SuspendingApiClientImpl : SuspendingApiClient {

		override suspend fun searchRepositories(query: String): List<GithubRepository> =
				get("https://api.github.com/search/repositories?q=${query}")
				.jsonObject
				.getJSONArray("items")
				.toRepos()

		override suspend fun login(auth: Authorization): GithubUser {
			val response = get("https://api.github.com/user", auth = auth)
			if (response.statusCode != 200) {
				throw RuntimeException("Incorrect login or password")
			}

			val jsonObject = response.jsonObject
			with(jsonObject) {
				return GithubUser(getString("login"), getInt("id"),
						getString("repos_url"), getString("name"))
			}
		}


		override suspend fun getRepositories(reposUrl: String, auth: Authorization): List<GithubRepository>
				= (get(reposUrl, auth = auth).jsonArray).toRepos()
	}
}