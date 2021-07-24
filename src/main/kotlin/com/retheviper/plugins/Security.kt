package com.retheviper.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import com.retheviper.application.configuration.security.JwtConfig
import com.retheviper.application.configuration.security.RoleBasedAuthorization
import com.retheviper.common.exception.AuthorizationException
import com.retheviper.common.role.Role
import com.retheviper.domain.dto.MemberPrincipal
import com.retheviper.infrastructure.repository.member.MemberPrincipalRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import mu.KotlinLogging
import java.util.*

fun Application.configureSecurity() {

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(
                JwtConfig.verifier
            )
            validate { credential ->
                credential.payload.getClaim("userId")?.let { MemberPrincipalRepository.find(it.asString()) }
            }
        }
    }

    install(RoleBasedAuthorization) {
        getRoles { (it as MemberPrincipal).roles }
    }
}