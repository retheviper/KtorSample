package com.retheviper.route

import com.retheviper.common.constant.Constants
import com.retheviper.route.auth.request.MemberCredentialForm
import com.retheviper.route.member.model.request.MemberUpsertForm
import com.retheviper.testbase.KtorTestBase
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthRouteTest : KtorTestBase() {

    private val path = Constants.API_BASE_PATH

    @BeforeAll
    fun createMember() {
        runTest {
            handleRequest(HttpMethod.Post, "$path/members") {
                requestBody(
                    MemberUpsertForm(
                        userId = testLoginUserId,
                        name = testLoginName,
                        password = testLoginPassword
                    )
                )
            }
        }
    }

    @Test
    fun login() {
        runTestWithSecurity {
            with(handleRequest(HttpMethod.Post, "$path/login") {
                requestBody(
                    MemberCredentialForm(
                        username = testLoginUserId,
                        password = testLoginPassword
                    )
                )
            }) {
                assertEquals(
                    actual = response.status(),
                    expected = HttpStatusCode.OK
                )
                assertNotNull(response.headers["X-Auth-Token"])
            }
        }
    }
}