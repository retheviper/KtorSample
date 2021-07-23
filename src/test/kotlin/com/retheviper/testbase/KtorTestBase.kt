package com.retheviper.testbase

import com.retheviper.plugins.configureDatabase
import com.retheviper.plugins.configureRouting
import com.retheviper.plugins.configureSecurity
import com.retheviper.plugins.configureSerialization
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

open class KtorTestBase {

    var id = 2

    val testUserId = "testUserId"

    val testName = "testName"

    val testPassword = "testPassword"

    val testLoginUserId = "testLoginUserId"

    val testLoginName = "testLoginName"

    val testLoginPassword = "testLoginPassword"

    /**
     * Run test without security settings.
     */
    fun runTest(handle: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            configureRouting()
            configureDatabase()
            configureSerialization()
        }) {
            handle(this)
        }
    }

    /**
     * Run test with security settings.
     */
    fun runTestWithSecurity(handle: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            configureRouting()
            configureDatabase()
            configureSecurity()
            configureSerialization()
        }) {
            handle(this)
        }
    }

    /**
     * Create JSON request body.
     */
    inline fun <reified T> TestApplicationRequest.requestBody(body: T) {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(
            Json.encodeToString(body)
        )
    }
}