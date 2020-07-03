package com.epam.talks.github.model

import android.util.Base64
import android.util.Base64.NO_WRAP
import android.util.Log
import com.epam.talks.github.GithubRepository
import com.epam.talks.github.GithubUser
import com.github.kittinunf.fuel.core.Request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.FuelJson
import com.github.kittinunf.fuel.json.responseJson
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

import kotlin.coroutines.CoroutineContext

data class Authorization(
		val login: String,
		val password: String
)

interface ApiClient: CoroutineScope {

	fun login(auth: Authorization) : Deferred<GithubUser>
	fun getRepositories(reposUrl: String, auth: Authorization) : Deferred<List<GithubRepository>>
	fun searchRepositories(searchQuery: String) : Deferred<List<GithubRepository>>

	class ApiClientImpl(override val coroutineContext: CoroutineContext) : ApiClient {

		private val ioScope = CoroutineScope(Dispatchers.IO)

		override fun searchRepositories(query: String): Deferred<List<GithubRepository>> = ioScope.async {
			val request = "https://api.github.com/search/repositories?q=${query}".httpGet()
			val (req, response, result) = request.responseJson()
			val (data, error) = result
			try {
				data?.let {
					return@async it.obj().getJSONArray("items").toRepos()
				}
			} catch (e: Exception) {
				Log.e("TAG", "Failed to read data", e);
			}
			return@async emptyList<GithubRepository>()
		}


		override fun login(auth: Authorization): Deferred<GithubUser> = ioScope.async {
			val request = "https://api.github.com/user".httpGet()
			addAuth(request, auth)
			val (req, response, result) = request.responseJson()
			val (data, error) = result
			val fuelJson = data as FuelJson

			with(fuelJson.obj()) {
				return@async GithubUser(getString("login"), getInt("id"),
						getString("repos_url"), getString("name"))
			}
		}


		override fun getRepositories(reposUrl: String, auth: Authorization): Deferred<List<GithubRepository>> = ioScope.async {
			val request = reposUrl.httpGet()
			addAuth(request, auth)
			val (req, response, result) = request.responseJson()
			val (data, error) = result
			return@async (data as FuelJson).toRepos()
		}

		private fun addAuth(request: Request, auth: Authorization) {
			request.header("Authorization", "Basic " + Base64.encodeToString("${auth.login}:${auth.password}".toByteArray(), NO_WRAP))
		}

	}
}
