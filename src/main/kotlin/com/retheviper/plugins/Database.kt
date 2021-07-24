package com.retheviper.plugins

import com.retheviper.common.role.Role
import com.retheviper.domain.dto.MemberDto
import com.retheviper.infrastructure.repository.member.MemberRepository
import com.retheviper.infrastructure.table.Member
import com.retheviper.infrastructure.table.MemberRole
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun configureDatabase() {

    Database.connect(
        url = "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;",
        driver = "org.h2.Driver"
    )

    transaction {
        SchemaUtils.create(
            Member, MemberRole
        )
        createAdmin()
    }
}

fun createAdmin() {
    MemberRepository.create(
        MemberDto(
            userId = "admin",
            name = "admin",
            password = "1234",
            role = setOf(Role.ADMIN, Role.USER)
        )
    )
}