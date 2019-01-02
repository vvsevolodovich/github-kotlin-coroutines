package com.epam.talks.github.presenters

import com.epam.talks.github.GithubRepository
import com.epam.talks.github.GithubUser
import com.epam.talks.github.model.ApiClient
import com.epam.talks.github.model.SuspendingApiClient
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.*

class SuspendingLoginPresenterTest {

	@Test
	fun testLogin() = runBlocking {
		val apiClient = mockk<SuspendingApiClient.SuspendingApiClientImpl>()
		val githubUser = GithubUser("login", 1, "url", "name")
		val repositories = GithubRepository(1, "repos_name", "full_repos_name")

		coEvery { apiClient.login(any()) } returns githubUser
		coEvery { apiClient.getRepositories(any(), any()) } returns Arrays.asList(repositories)

		val loginPresenterImpl = SuspendingLoginPresenterImpl(apiClient, newSingleThreadContext("testPoolSuspending"))
		runBlocking {
			val repos = loginPresenterImpl.doLogin("login", "password")
			assertNotNull(repos)
		}
	}
}
