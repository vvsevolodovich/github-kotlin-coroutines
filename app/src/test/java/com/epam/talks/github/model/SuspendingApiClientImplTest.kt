package com.epam.talks.github.model

import io.mockk.every
import io.mockk.mockk
import io.mockk.staticMockk
import io.mockk.use
import khttp.get
import khttp.responses.GenericResponse
import khttp.structures.authorization.BasicAuthorization
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SuspendingApiClientImplTest {

	private val loginJson = "{ \"login\": \"login\", \"id\": 1, \"repos_url\": \"url\", \"name\": \"name\" }"

	@Test
	fun login() = runBlocking {
		val apiClientImpl = SuspendingApiClient.SuspendingApiClientImpl()
		val genericResponse = mockLoginResponse()

		staticMockk("khttp.KHttp").use {
			every { get("https://api.github.com/user", auth = any()) } returns genericResponse

			val githubUser =
					apiClientImpl
						.login(BasicAuthorization("login", "pass"))

			assertNotNull(githubUser)
			assertEquals("name", githubUser.name)
			assertEquals("url", githubUser.repos_url)
		}
	}

	private fun mockLoginResponse(): GenericResponse {
		val genericResponse = mockk<GenericResponse>()
		every { genericResponse.jsonObject } returns JSONObject(loginJson)
		every { genericResponse.statusCode } returns 200
		return genericResponse
	}

	@Test
	fun getRepositories() {
	}

}