package com.retheviper.route.auth

import com.retheviper.common.constant.Constants
import com.retheviper.common.constant.Headers
import com.retheviper.infrastructure.repository.member.MemberPrincipalRepository
import com.retheviper.plugins.JwtConfig
import com.retheviper.route.auth.request.MemberCredentialForm
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.auth() {

    val path = Constants.API_BASE_PATH

    /**
     * Login
     */
    post("${path}/login") {
        val credentials = call.receive<MemberCredentialForm>()
        val user = MemberPrincipalRepository.find(
            userId = credentials.username,
            password = credentials.password
        )

        if (user == null)
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Invalid user"
            )
        else
            JwtConfig.makeToken(user).let {
                call.response.headers.append(
                    name = Headers.TOKEN,
                    value = it
                )
                call.respond(HttpStatusCode.OK)
            }
    }
}