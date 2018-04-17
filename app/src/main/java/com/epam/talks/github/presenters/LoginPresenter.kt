package com.epam.talks.github.presenters

interface LoginPresenter {

	suspend fun doLogin(login: String, pass: String) : List<String>


}