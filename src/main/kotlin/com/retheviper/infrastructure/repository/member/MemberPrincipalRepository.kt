package com.retheviper.infrastructure.repository.member

import com.retheviper.common.extension.verifyWith
import com.retheviper.domain.dto.MemberDto
import com.retheviper.domain.dto.MemberPrincipal
import com.retheviper.infrastructure.table.Member
import org.jetbrains.exposed.sql.transactions.transaction

object MemberPrincipalRepository {

    fun find(userId: String): MemberPrincipal? =
        transaction { MemberRepository.findOne { Member.userId eq userId }?.toPrincipal() }

    fun find(userId: String, password: String): MemberPrincipal? =
        transaction {
            val existing = MemberRepository.findOne { Member.userId eq userId }

            when {
                existing == null -> null

                password.verifyWith(existing.password) -> existing.toPrincipal()

                else -> null
            }
        }

    private fun MemberDto.toPrincipal(): MemberPrincipal =
        MemberPrincipal(
            id = checkNotNull(id),
            username = userId,
            name = name,
            roles = role
        )
}