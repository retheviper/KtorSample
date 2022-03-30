package com.retheviper.application.route.auth

import com.retheviper.application.configuration.security.JwtConfig
import com.retheviper.application.route.auth.model.request.MemberCredentialForm
import com.retheviper.common.constant.Authrorizer
import com.retheviper.common.constant.Constants
import com.retheviper.common.constant.Headers
import com.retheviper.common.extension.withRole
import com.retheviper.common.role.Role
import com.retheviper.domain.dto.MemberPrincipal
import com.retheviper.infrastructure.repository.member.MemberPrincipalRepository
import io.ktor.application.*
import io.ktor.auth.*
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
                    value = "Bearer $it"
                )
                call.response.status(HttpStatusCode.OK)
            }
    }

    /**
     * Test endpoint
     */
    authenticate(Authrorizer.JWT) {
        withRole(Role.USER) {
            get("$path/user") {
                val principal = call.principal<MemberPrincipal>()
                call.respond("${principal?.username}, you are USER!")
            }
        }

        withRole(Role.ADMIN) {
            get("$path/admin") {
                val principal = call.principal<MemberPrincipal>()
                call.respond("${principal?.username}, you are ADMIN!")
            }
        }
    }
}