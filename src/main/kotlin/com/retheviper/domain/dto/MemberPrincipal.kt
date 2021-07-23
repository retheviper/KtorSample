package com.retheviper.domain.dto

import io.ktor.auth.*

data class MemberPrincipal(
    val id: Int,
    val username: String,
    val name: String,
    val roles: Set<String> = emptySet()
) : Principal