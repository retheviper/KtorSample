package com.retheviper.plugins

import com.retheviper.application.route.auth.model.response.AuthFailedResponse
import com.retheviper.common.exception.AuthorizationException
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*


fun Application.configureException() {
    install(StatusPages) {
        exception<Throwable> { cause ->
            when (cause) {
                is AuthorizationException -> {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = AuthFailedResponse(
                            code = "", // TODO
                            reason = cause.message
                        )
                    )
                }
            }
        }
    }
}