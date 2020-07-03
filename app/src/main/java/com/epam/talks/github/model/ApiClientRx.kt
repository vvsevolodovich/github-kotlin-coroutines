package com.epam.talks.github.model

import com.epam.talks.github.GithubRepository
import com.epam.talks.github.GithubUser
import io.reactivex.Observable
import io.reactivex.Single

interface ApiClientRx {

	fun login(auth: Authorization) : Single<GithubUser>
	fun getRepositories(reposUrl: String, auth: Authorization) : Single<List<GithubRepository>>
	fun searchRepositories(query: String) : Observable<List<GithubRepository>>

	class ApiClientRxImpl : ApiClientRx {
		override fun searchRepositories(query: String): Observable<List<GithubRepository>> {
			return Observable.fromCallable {
				ArrayList<GithubRepository>()
			}
		}

		override fun login(auth: Authorization): Single<GithubUser> = Single.fromCallable {
			GithubUser("dummy", 1,
						"dummy_url", "dummy_name")
		}

		override fun getRepositories(reposUrl: String, auth: Authorization): Single<List<GithubRepository>> {
			return Single.fromCallable({
				ArrayList<GithubRepository>()
			})
		}
	}
}