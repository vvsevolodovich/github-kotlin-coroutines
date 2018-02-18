package com.epam.talks.github.model

import com.epam.talks.github.GithubRepository
import com.epam.talks.github.GithubUser
import io.reactivex.Single
import khttp.get
import khttp.structures.authorization.Authorization

interface ApiClientRx {

	fun login(auth: Authorization) : Single<GithubUser>
	fun getRepositories(reposUrl: String, auth: Authorization) : Single<List<GithubRepository>>

	class ApiClientRxImpl : ApiClientRx {

		override fun login(auth: Authorization): Single<GithubUser> = Single.fromCallable {
			val response = get("https://api.github.com/user", auth = auth)
			if (response.statusCode != 200) {
				throw RuntimeException("Incorrect login or password")
			}

			val jsonObject = response.jsonObject
			with(jsonObject) {
				return@with GithubUser(getString("login"), getInt("id"),
						getString("repos_url"), getString("name"))
			}
		}

		override fun getRepositories(reposUrl: String, auth: Authorization): Single<List<GithubRepository>> {
			return Single.fromCallable({
				(get(reposUrl, auth = auth).jsonArray).toRepos()
			})
		}
	}
}