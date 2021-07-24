package com.retheviper.application.route.auth.model.request

import kotlinx.serialization.Serializable

@Serializable
data class MemberCredentialForm(
    val username: String,
    val password: String
)