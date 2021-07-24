package com.retheviper.domain.dto

import com.retheviper.common.role.Role
import io.ktor.auth.*

data class MemberPrincipal(
    val id: Int,
    val username: String,
    val name: String,
    val roles: Set<Role> = emptySet()
) : Principal