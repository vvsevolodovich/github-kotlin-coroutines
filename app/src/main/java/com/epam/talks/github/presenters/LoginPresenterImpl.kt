package com.epam.talks.github.presenters

import com.epam.talks.github.model.ApiClient
import com.epam.talks.github.model.Authorization

class LoginPresenterImpl(val apiClient: ApiClient) : LoginPresenter {

	fun showProgress(show: Boolean) {

	}

	override suspend fun doLogin(login: String, pass: String): List<String> {
		val auth = Authorization(login, pass)
		val userInfo = apiClient.login(auth).await()
		val repoUrl = userInfo.repos_url
		val list = apiClient.getRepositories(repoUrl, auth).await()
		return list.map { it -> it.full_name }
	}
}