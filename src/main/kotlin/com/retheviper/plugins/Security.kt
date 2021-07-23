package com.retheviper.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import com.retheviper.domain.dto.MemberPrincipal
import com.retheviper.infrastructure.repository.member.MemberPrincipalRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
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
}

object JwtConfig {

    private const val secret = "a23lI92eEMw2PQ96S"

    private const val issuer = "ktor-sample"

    private const val validityInMs = 36_000_00 * 10 // 10 hours

    private val algorithm = Algorithm.HMAC512(secret)

    val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(issuer).build()

    /**
     * Produce a token.
     */
    fun makeToken(principal: MemberPrincipal): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("id", principal.id)
        .withClaim("userId", principal.username)
        .withArrayClaim("roles", principal.roles.toTypedArray())
        .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
        .sign(algorithm)
}