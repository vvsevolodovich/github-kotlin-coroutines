package com.epam.talks.github.model

import com.epam.talks.github.GithubRepository
import com.epam.talks.github.GithubUser
import khttp.get
import khttp.structures.authorization.Authorization

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

interface ApiClient {

	fun login(auth: Authorization) : Deferred<GithubUser>
	fun getRepositories(reposUrl: String, auth: Authorization) : Deferred<List<GithubRepository>>

	class ApiClientImpl : ApiClient {

		override fun login(auth: Authorization): Deferred<GithubUser> = async {
			val response = get("https://api.github.com/user", auth = auth)
			if (response.statusCode != 200) {
				throw RuntimeException("Incorrect login or password")
			}

			val jsonObject = response.jsonObject
			with(jsonObject) {
				return@async GithubUser(getString("login"), getInt("id"),
						getString("repos_url"), getString("name"))
			}
		}


		override fun getRepositories(reposUrl: String, auth: Authorization): Deferred<List<GithubRepository>> = async {
			return@async (get(reposUrl, auth = auth).jsonArray).toRepos()
		}

	}
}
