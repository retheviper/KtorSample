package com.retheviper.application.route.auth.model.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthFailedResponse(
    val code: String,
    val reason: String
)