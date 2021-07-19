package com.retheviper.route.member.model.request

import com.retheviper.domain.dto.MemberDto
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
) {

    /**
     * Map to DTO.
     */
    fun toDto(): MemberDto =
        MemberDto(
            userId = userId,
            name = name,
            password = password,
            newPassword = newPassword,
        )

    /**
     * Map to DTO with ID.
     */
    fun toDto(id: Int): MemberDto =
        MemberDto(
            id = id,
            userId = userId,
            name = name,
            password = password,
            newPassword = newPassword,
        )
}
