package com.retheviper

import com.retheviper.plugins.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        configureDatabase()
        configureSecurity()
        configureRouting()
        configureSerialization()
        configureException()
    }.start(wait = true)
}
