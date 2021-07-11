package com.retheviper.route.member.model.request

import kotlinx.serialization.Serializable

@Serializable
data class MemberUpsertForm(

    /**
     * Member's ID (User ID)
     */
    val userId: String,

    /**
     * Member's real name
     */
    val name: String,

    /**
     * Member's password
     */
    val password: String,

    /**
     * Member's new password
     */
    val newPassword: String? = null
)
