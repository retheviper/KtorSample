package com.retheviper.infrastructure.table

import com.retheviper.infrastructure.table.audit.Audit
import com.retheviper.domain.dto.MemberDto
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow

object Member : Audit() {
    val userId: Column<String> = varchar(name = "user_id", length = 16)
    val name: Column<String> = varchar(name = "name", length = 16)
    val password: Column<String> = varchar(name = "password", length = 255)
    val memberInformationId: Column<Int?> = integer("member_information_id").nullable()
    val accountNonExpired: Column<Boolean> = bool("account_non_expired")
    val accountNonLocked: Column<Boolean> = bool("account_non_locked")
    val credentialsNonExpired: Column<Boolean> = bool("credentials_non_expired")
    val enabled: Column<Boolean> = bool("enabled")

    /**
     * Map query result to DTO.
     */
    fun toDto(row: ResultRow): MemberDto =
        MemberDto(
            id = row[id].value,
            userId = row[userId],
            name = row[name],
            password = row[password],
            memberInformationId = row[memberInformationId],
            accountNonExpired = row[accountNonExpired],
            accountNonLocked = row[accountNonLocked],
            credentialsNonExpired = row[credentialsNonExpired],
            enabled = row[enabled]
        )
}
