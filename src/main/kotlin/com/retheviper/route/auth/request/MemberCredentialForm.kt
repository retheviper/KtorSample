package com.retheviper.route.auth.request

import kotlinx.serialization.Serializable

@Serializable
data class MemberCredentialForm(
    val username: String,
    val password: String
)