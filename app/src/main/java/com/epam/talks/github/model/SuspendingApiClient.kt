package com.epam.talks.github.model

import com.epam.talks.github.GithubRepository
import com.epam.talks.github.GithubUser
import com.github.kittinunf.fuel.httpGet

interface SuspendingApiClient {

	suspend fun login(auth: Authorization) : GithubUser
	suspend fun getRepositories(reposUrl: String, auth: Authorization) : List<GithubRepository>
	suspend fun searchRepositories(searchQuery: String) : List<GithubRepository>

	class SuspendingApiClientImpl : SuspendingApiClient {

		override suspend fun searchRepositories(query: String): List<GithubRepository> =
				ArrayList<GithubRepository>()
				//"https://api.github.com/search/repositories?q=${query}".httpGet()


		override suspend fun login(auth: Authorization): GithubUser =
			GithubUser("login", 1,
						"repos_url","name")



		override suspend fun getRepositories(reposUrl: String, auth: Authorization): List<GithubRepository>
				= ArrayList<GithubRepository>()
	}
}