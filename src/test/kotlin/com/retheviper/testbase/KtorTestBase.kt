package com.retheviper.testbase

import com.retheviper.plugins.configureDatabase
import com.retheviper.plugins.configureRouting
import com.retheviper.plugins.configureSerialization
import io.ktor.server.testing.*

open class KtorTestBase {

    fun runTest(handle: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            configureRouting()
            configureDatabase()
            configureSerialization()
        }) {
            handle(this)
        }
    }
}