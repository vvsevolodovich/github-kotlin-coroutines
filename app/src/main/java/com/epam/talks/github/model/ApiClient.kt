package com.epam.talks.github.model

import com.epam.talks.github.GithubRepository
import com.epam.talks.github.GithubUser
import khttp.get
import khttp.structures.authorization.Authorization
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

import java.util.*
import kotlin.coroutines.CoroutineContext

interface ApiClient: CoroutineScope {

	fun login(auth: Authorization) : Deferred<GithubUser>
	fun getRepositories(reposUrl: String, auth: Authorization) : Deferred<List<GithubRepository>>
	fun searchRepositories(searchQuery: String) : Deferred<List<GithubRepository>>

	class ApiClientImpl(override val coroutineContext: CoroutineContext) : ApiClient {

		override fun searchRepositories(query: String): Deferred<List<GithubRepository>> = async {
			val jsonObject = get("https://api.github.com/search/repositories?q=${query}")
					.jsonObject
			if (jsonObject.has("items")) {
				return@async jsonObject
						.getJSONArray("items")
						.toRepos()
			}
			return@async ArrayList<GithubRepository>()
		}

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
