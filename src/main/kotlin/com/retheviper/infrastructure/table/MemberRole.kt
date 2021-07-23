package com.retheviper.infrastructure.table

import com.retheviper.infrastructure.table.audit.Audit
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption

object MemberRole : Audit() {
    val memberId: Column<EntityID<Int>> = reference(
        name = "member_id",
        foreign = Member,
        onDelete = ReferenceOption.CASCADE
    )
    val role: Column<String> = varchar(name = "role", length = 255)
}