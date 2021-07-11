package com.retheviper.route.member.model.dto

data class MemberDto(
    val id: Int?,
    val userId: String,
    val name: String,
    val password: String,
    val memberInformationId: Int?,
    val accountNonExpired: Boolean,
    val accountNonLocked: Boolean,
    val credentialsNonExpired: Boolean,
    val enabled: Boolean,
)
