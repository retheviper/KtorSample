package com.retheviper.application.route

import com.retheviper.application.route.auth.model.request.MemberCredentialForm
import com.retheviper.common.constant.Constants
import com.retheviper.common.constant.Headers
import com.retheviper.domain.dto.MemberDto
import com.retheviper.infrastructure.repository.member.MemberRepository
import com.retheviper.plugins.configureDatabase
import com.retheviper.testbase.KtorTestBase
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.http.*
import io.ktor.server.testing.*

class AuthRouteTest : KtorTestBase() {

    private val path = Constants.API_BASE_PATH

    init {
        beforeSpec {
            configureDatabase()
            MemberRepository.create(
                MemberDto(
                    userId = testLoginUserId,
                    name = testLoginName,
                    password = testLoginPassword
                )
            )
        }

        "login" {
            runTest {
                with(handleRequest(HttpMethod.Post, "$path/login") {
                    requestBody(
                        MemberCredentialForm(
                            username = testLoginUserId,
                            password = testLoginPassword
                        )
                    )
                }) {
                    response.status() shouldBe HttpStatusCode.OK
                    response.headers[Headers.TOKEN] shouldNotBe null
                }
            }
        }
    }
}