package com.retheviper

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.retheviper.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        configureDatabase()
        configureRouting()
//        configureSecurity()
        configureSerialization()
    }.start(wait = true)
}
