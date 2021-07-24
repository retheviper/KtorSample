package com.retheviper.plugins

import com.retheviper.application.configuration.security.JwtConfig
import com.retheviper.application.configuration.security.RoleBasedAuthorization
import com.retheviper.domain.dto.MemberPrincipal
import com.retheviper.infrastructure.repository.member.MemberPrincipalRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*

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