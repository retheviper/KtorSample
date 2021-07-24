package com.retheviper.application.route

import com.retheviper.application.route.member.model.request.MemberUpsertForm
import com.retheviper.application.route.member.model.response.MemberResponse
import com.retheviper.common.constant.Constants
import com.retheviper.infrastructure.repository.member.MemberRepository
import com.retheviper.plugins.configureDatabase
import com.retheviper.testbase.KtorTestBase
import io.kotest.core.test.TestCaseOrder
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MemberRouteTest : KtorTestBase() {

    override fun testCaseOrder(): TestCaseOrder = TestCaseOrder.Sequential

    private val path = "${Constants.API_BASE_PATH}/members"

    init {
        beforeSpec {
            configureDatabase()
            id = MemberRepository.findAll().size + 1
        }

        "createMember" {
            runTest {
                with(handleRequest(HttpMethod.Post, path) {
                    requestBody(
                        MemberUpsertForm(
                            userId = testUserId,
                            name = testName,
                            password = testPassword
                        )
                    )
                }) {
                    response.status() shouldBe HttpStatusCode.OK
                    response.content shouldBe Json.encodeToString(
                        MemberResponse(
                            id = id,
                            userId = testUserId,
                            name = testName
                        )
                    )
                }
            }
        }

        "getMember" {
            runTest {
                with(handleRequest(HttpMethod.Get, "$path/$id")) {
                    response.status() shouldBe HttpStatusCode.OK
                    response.content shouldBe Json.encodeToString(
                        MemberResponse(
                            id = id,
                            userId = testUserId,
                            name = testName
                        )
                    )
                }
            }
        }

        "updateMember" {
            runTest {
                with(handleRequest(HttpMethod.Put, "$path/$id") {
                    requestBody(
                        MemberUpsertForm(
                            userId = testUserId.reversed(),
                            name = testName.reversed(),
                            password = testPassword,
                            newPassword = testPassword.reversed()
                        )
                    )
                }) {
                    response.status() shouldBe HttpStatusCode.OK
                    response.content shouldBe Json.encodeToString(
                        MemberResponse(
                            id = id,
                            userId = testUserId.reversed(),
                            name = testName.reversed()
                        )
                    )
                }
            }
        }

        "listMember" {
            runTest {
                with(handleRequest(HttpMethod.Get, path)) {
                    response.status() shouldBe HttpStatusCode.OK
                    val body: Array<MemberResponse> = Json.decodeFromString(checkNotNull(response.content))
                    body shouldContain MemberResponse(
                        id = id,
                        userId = testUserId.reversed(),
                        name = testName.reversed()
                    )
                }
            }
        }

        "deleteMember" {
            runTest {
                with(handleRequest(HttpMethod.Delete, "$path/$id") {
                    requestBody(
                        MemberUpsertForm(
                            userId = testUserId.reversed(),
                            name = testName.reversed(),
                            password = testPassword.reversed()
                        )
                    )
                }) {
                    response.status() shouldBe HttpStatusCode.OK
                    response.content shouldBe "$id successfully deleted"

                }
            }
        }
    }
}