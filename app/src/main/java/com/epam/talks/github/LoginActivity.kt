package com.epam.talks.github

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.epam.talks.github.model.ApiClient
import com.epam.talks.github.model.ApiClientRx
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import khttp.structures.authorization.BasicAuthorization
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class LoginActivity : AppCompatActivity() {


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
			//attemptLogin()
			attemptLoginRx()
		}
	}

	private fun attemptLoginRx() {
		val login = email.text.toString()
		val pass = password.text.toString()

		showProgress(true)
		val auth = BasicAuthorization(login, pass)
		val apiClient = ApiClientRx.ApiClientRxImpl()
		apiClient.login(auth)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.doFinally {
					showProgress(false)
				}
				.flatMap {
					return@flatMap apiClient.getRepositories(it.repos_url, auth)
							.subscribeOn(Schedulers.io())
							.observeOn(AndroidSchedulers.mainThread())
				}
				.subscribe({
					val list = it
					showRepositories(this@LoginActivity, list!!.map { it -> it.full_name })
				}, {
					Log.e("TAG", "Failed to show repos", it)
				})
	}

	private fun attemptLogin() {
		val login = email.text.toString()
		val pass = password.text.toString()
		val apiClient = ApiClient.ApiClientImpl()
		launch(UI) {
			showProgress(true)
			val auth = BasicAuthorization(login, pass)
			try {
				val userInfo = apiClient.login(auth).await()
				val repoUrl = userInfo!!.repos_url
				val list = apiClient.getRepositories(repoUrl, auth).await()
				showRepositories(this@LoginActivity, list!!.map { it -> it.full_name })
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
