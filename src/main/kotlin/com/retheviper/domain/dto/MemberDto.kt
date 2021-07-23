package com.retheviper.domain.dto

import com.retheviper.common.role.Role

data class MemberDto(
    val id: Int? = null,
    val userId: String,
    val name: String,
    val password: String,
    val newPassword: String? = null,
    val memberInformationId: Int? = null,
    val accountNonExpired: Boolean? = true,
    val accountNonLocked: Boolean? = true,
    val credentialsNonExpired: Boolean? = true,
    val enabled: Boolean? = true,
    val deleted: Boolean? = false,
    val role: List<Role> = emptyList()
)
