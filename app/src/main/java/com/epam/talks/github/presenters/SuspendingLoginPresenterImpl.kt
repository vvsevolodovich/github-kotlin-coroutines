package com.epam.talks.github.presenters

import com.epam.talks.github.model.SuspendingApiClient
import khttp.structures.authorization.BasicAuthorization
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class SuspendingLoginPresenterImpl(val apiClient: SuspendingApiClient, val context: CoroutineContext = newSingleThreadContext("LoginPresenterPool")) : LoginPresenter {

	fun showProgress(show: Boolean) {

	}

	override suspend fun doLogin(login: String, pass: String): List<String> {
		val auth = BasicAuthorization(login, pass)
		val userInfo = withContext(context) { apiClient.login(auth) }
		val repoUrl = userInfo.repos_url
		val list = withContext(context) { apiClient.getRepositories(repoUrl, auth) }
		return list.map { it -> it.full_name }
	}
}