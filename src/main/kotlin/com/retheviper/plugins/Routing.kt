package com.retheviper.plugins

import com.retheviper.common.constant.Constants
import com.retheviper.domain.dto.MemberPrincipal
import com.retheviper.route.auth.auth
import com.retheviper.route.member.members
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureRouting() {

    routing {

        get(Constants.API_BASE_PATH) {
            call.respond("This is API base path!")
        }

        members()
        auth()
    }
}
