package com.epam.talks.github

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import khttp.get
import khttp.structures.authorization.Authorization
import khttp.structures.authorization.BasicAuthorization
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.util.*

class LoginActivity : AppCompatActivity() {

	private var mAuthTask: UserLoginTask? = null

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

		email_sign_in_button.setOnClickListener { attemptLogin() }
	}

	private fun attemptLogin() {
		if (mAuthTask != null) {
			return
		}

		val login = email.text.toString()
		val pass = password.text.toString()
		launch(UI) {
			showProgress(true)
			val auth = BasicAuthorization(login, pass)
			try {
				val userInfo = login(auth).await()
				val repoUrl = userInfo!!.repos_url
				val list = getRepositories(repoUrl, auth).await()
				showRepositories(this@LoginActivity, list!!.map { it -> it.full_name })
			} catch (e: RuntimeException) {
				Toast.makeText(this@LoginActivity, e.message, LENGTH_LONG).show()
			} finally {
				showProgress(false)
			}
		}
	}

	private fun login(auth: Authorization) : Deferred<GithubUser?> = async {
		val response = get("https://api.github.com/user", auth = auth)
		if (response.statusCode != 200) {
			throw RuntimeException("Incorrect login or password")
		}

		val jsonObject = response.jsonObject
		with (jsonObject) {
			return@async GithubUser(getString("login"), getInt("id"),
					getString("repos_url"), getString("name"))
		}
	}

	private fun getRepositories(repoUrl : String, authorization: BasicAuthorization) : Deferred<List<GithubRepository>?> = async {
		val jsonArray = get(repoUrl, auth = authorization).jsonArray
		val repos = ArrayList<GithubRepository>(jsonArray.length())

		for (i in 0..(jsonArray.length() - 1)) {
			val item = jsonArray.getJSONObject(i)
			with(item) {
				repos.add(GithubRepository(getInt("id"),
						getString("name"),
						getString("full_name")
				)
				)
			}
		}

		return@async repos
	}

	private fun showProgress(show: Boolean) {
		if (show) {
			login_progress.visibility = View.VISIBLE
		} else {
			login_progress.visibility = View.GONE
		}
	}



	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	inner class UserLoginTask internal constructor(private val mEmail: String, private val mPassword: String) : AsyncTask<Void, Void, Boolean>() {

		override fun doInBackground(vararg params: Void): Boolean? {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(5000)
			} catch (e: InterruptedException) {
				return false
			}

			return DUMMY_CREDENTIALS
					.map { it.split(":") }
					.firstOrNull { it[0] == mEmail }
					?.let {
						// Account exists, return true if the password matches.
						it[1] == mPassword
					}
					?: true
		}

		override fun onPostExecute(success: Boolean?) {
			mAuthTask = null
			showProgress(false)

			if (success!!) {
				finish()
			} else {
				password.error = getString(R.string.error_incorrect_password)
				password.requestFocus()
			}
		}

		override fun onCancelled() {
			mAuthTask = null
			showProgress(false)
		}
	}

	companion object {

		/**
		 * A dummy authentication store containing known user names and passwords.
		 * TODO: remove after connecting to a real authentication system.
		 */
		private val DUMMY_CREDENTIALS = arrayOf("foo@example.com:hello", "bar@example.com:world")
	}

	//companion object {}

}
