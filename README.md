# github-kotlin-coroutines

This project demonstrate how to use coroutines of Kotlin to perform async work in Android application.
The best thing about them is that you can write async code in a sync style. The excerpt you're most interested in is:

```kotlin
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
```

login() and getRepositories() method are performed off the main thread, but the only thing you need to bother about is using .await() call after. 
