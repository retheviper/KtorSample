package com.retheviper.application.configuration.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import com.retheviper.domain.dto.MemberPrincipal
import java.util.*

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
        .withClaim("userId", principal.username)
        .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
        .sign(algorithm)
}