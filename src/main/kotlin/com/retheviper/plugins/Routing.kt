package com.retheviper.plugins

import com.retheviper.common.constant.Constants
import com.retheviper.route.member.members
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureRouting() {

    routing {
        get(Constants.API_BASE_PATH) {
            call.respond("Hello, Ktor!")
        }
        members()
    }
}
