package com.retheviper.plugins

import io.ktor.application.*
import io.ktor.features.*
import org.slf4j.event.Level

fun Application.configureLogging() {
    install(CallLogging) {
        level = Level.INFO
        format {
            """
                [${it.callId}] ${it.response.status()}: ${it.request.local.method} - ${it.request.local.uri}
            """.trimIndent()
        }
    }
}