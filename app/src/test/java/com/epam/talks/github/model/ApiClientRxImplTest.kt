package com.epam.talks.github.model

import io.mockk.every
import io.mockk.mockk
import io.mockk.staticMockk
import io.mockk.use
import khttp.get
import khttp.responses.GenericResponse
import khttp.structures.authorization.BasicAuthorization
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test


class ApiClientRxImplTest {

	private val loginJson = "{ \"login\": \"login\", \"id\": 1, \"repos_url\": \"url\", \"name\": \"name\" }"

	@Test
	fun login() {
		val apiClientImpl = ApiClientRx.ApiClientRxImpl()
		val genericResponse = mockLoginResponse()

		staticMockk("khttp.KHttp").use {
			every { get("https://api.github.com/user", auth = any()) } returns genericResponse

			val githubUser =
					apiClientImpl
							.login(BasicAuthorization("login", "pass"))

			githubUser.subscribe({ githubUser ->
				Assert.assertNotNull(githubUser)
				Assert.assertEquals("name", githubUser.name)
				Assert.assertEquals("url", githubUser.repos_url)
			})

		}
	}

	private fun mockLoginResponse(): GenericResponse {
		val genericResponse = mockk<GenericResponse>()
		every { genericResponse.jsonObject } returns JSONObject(loginJson)
		every { genericResponse.statusCode } returns 200
		return genericResponse
	}
}
