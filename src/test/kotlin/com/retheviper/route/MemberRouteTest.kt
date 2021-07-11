package com.retheviper.route

import com.retheviper.common.constant.Constants
import com.retheviper.route.member.model.request.MemberUpsertForm
import com.retheviper.route.member.model.response.MemberResponse
import com.retheviper.testbase.KtorTestBase
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.Test
import kotlin.test.assertEquals

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MemberRouteTest : KtorTestBase() {

    private val path = "${Constants.API_BASE_PATH}/members"

    private var id = 1

    private val testUserId = "testUserId"

    private val testName = "testName"

    private val testPassword = "testPassword"

    @Test
    @Order(4)
    fun listMember() {
        runTest {
            handleRequest(HttpMethod.Get, path).apply {
                assertEquals(
                    actual = response.status(),
                    expected = HttpStatusCode.OK
                )
                assertEquals(
                    actual = response.content,
                    expected = Json.encodeToString(
                        listOf(
                            MemberResponse(
                                id = id,
                                userId = testUserId.reversed(),
                                name = testName.reversed()
                            )
                        )
                    ),
                )
            }
        }
    }

    @Test
    @Order(2)
    fun getMember() {
        runTest {
            handleRequest(HttpMethod.Get, "$path/$id").apply {
                assertEquals(
                    actual = response.status(),
                    expected = HttpStatusCode.OK
                )
                assertEquals(
                    actual = response.content,
                    expected = Json.encodeToString(
                        MemberResponse(
                            id = id,
                            userId = testUserId,
                            name = testName
                        )
                    ),
                )
            }
        }
    }

    @Test
    @Order(1)
    fun createMember() {
        runTest {
            with(handleRequest(HttpMethod.Post, path) {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    Json.encodeToString(
                        MemberUpsertForm(
                            userId = testUserId,
                            name = testName,
                            password = testPassword
                        )
                    )
                )
            }) {
                assertEquals(
                    actual = response.status(),
                    expected = HttpStatusCode.OK
                )
                assertEquals(
                    actual = response.content,
                    expected = Json.encodeToString(
                        MemberResponse(
                            id = id,
                            userId = testUserId,
                            name = testName
                        )
                    ),
                )
            }
        }
    }

    @Test
    @Order(3)
    fun updateMember() {
        runTest {
            with(handleRequest(HttpMethod.Put, "$path/$id") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    Json.encodeToString(
                        MemberUpsertForm(
                            userId = testUserId.reversed(),
                            name = testName.reversed(),
                            password = testPassword,
                            newPassword = testPassword.reversed()
                        )
                    )
                )
            }) {
                assertEquals(
                    actual = response.status(),
                    expected = HttpStatusCode.OK
                )
                assertEquals(
                    actual = response.content,
                    expected = Json.encodeToString(
                        MemberResponse(
                            id = id,
                            userId = testUserId.reversed(),
                            name = testName.reversed()
                        )
                    ),
                )
            }
        }
    }

    @Test
    @Order(5)
    fun deleteMember() {
        runTest {
            with(handleRequest(HttpMethod.Delete, "$path/$id") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    Json.encodeToString(
                        testPassword.reversed()
                    )
                )
            }) {
                assertEquals(
                    actual = response.status(),
                    expected = HttpStatusCode.OK
                )
                assertEquals(
                    actual = response.content,
                    expected = "$id successfully deleted"
                )
            }
        }
    }
}