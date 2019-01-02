package com.epam.talks.github

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.epam.talks.github.model.ApiClient
import com.epam.talks.github.model.SuspendingApiClient
import com.epam.talks.github.presenters.LoginPresenter
import com.epam.talks.github.presenters.SuspendingLoginPresenterImpl
import khttp.structures.authorization.BasicAuthorization
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LoginActivity : AppCompatActivity(), CoroutineScope {

	override val coroutineContext: CoroutineContext
				get() = Dispatchers.Main

	val presenter: LoginPresenter = SuspendingLoginPresenterImpl(SuspendingApiClient.SuspendingApiClientImpl())

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)
		password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
			if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
				attemptLogin()
				return@OnEditorActionListener true
			}
			false
		})

		email_sign_in_button.setOnClickListener {
			attemptLogin()
		}
	}

	private fun attemptLoginSuspending() {
		val login = email.text.toString()
		val pass = password.text.toString()
		val apiClient = SuspendingApiClient.SuspendingApiClientImpl()
		launch {
			val repositoriesList = presenter.doLogin(login, pass)
			showRepositories(this@LoginActivity, repositoriesList)
		}
	}

	private fun attemptLogin() {
		val login = email.text.toString()
		val pass = password.text.toString()
		val apiClient = ApiClient.ApiClientImpl(coroutineContext)
		launch {
			showProgress(true)
			val auth = BasicAuthorization(login, pass)
			try {
				val userInfo = apiClient.login(auth).await()
				if (!isActive) {
					return@launch
				}
				val repoUrl = userInfo!!.repos_url
				val list = apiClient.getRepositories(repoUrl, auth).await()
				showRepositories(this@LoginActivity, list.map { it -> it.full_name })
			} catch (e: RuntimeException) {
				Toast.makeText(this@LoginActivity, e.message, LENGTH_LONG).show()
			} finally {
				showProgress(false)
			}
		}
	}

	private fun showProgress(show: Boolean) {
		if (show) {
			login_progress.visibility = View.VISIBLE
		} else {
			login_progress.visibility = View.GONE
		}
	}

}
