package com.epam.talks.github.presenters

import com.epam.talks.github.GithubRepository
import com.epam.talks.github.GithubUser
import com.epam.talks.github.model.ApiClient
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.*

class LoginPresenterTest {

@Test
fun testLogin() = {
	val apiClient = mockk<ApiClient.ApiClientImpl>()
	val githubUser = GithubUser("login", 1, "url", "name")
	val repositories = GithubRepository(1, "repos_name", "full_repos_name")

	coEvery { apiClient.login(any()).await() } returns githubUser
	coEvery { apiClient.getRepositories(any(), any()).await() } returns Arrays.asList(repositories)

	val loginPresenterImpl = LoginPresenterImpl(apiClient)
	runBlocking {
		val repos = loginPresenterImpl.doLogin("login", "password")
		assertNotNull(repos)
	}
}
}
