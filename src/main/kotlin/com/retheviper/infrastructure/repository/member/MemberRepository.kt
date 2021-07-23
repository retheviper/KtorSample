package com.retheviper.infrastructure.repository.member

import com.retheviper.common.extension.hash
import com.retheviper.common.role.Role
import com.retheviper.domain.dto.MemberDto
import com.retheviper.infrastructure.table.Member
import com.retheviper.infrastructure.table.MemberRole
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object MemberRepository {

    fun findAll(): List<MemberDto> =
        transaction {
            Member.selectAll().map(Member::toDto)
        }

    fun findOne(id: Int): MemberDto? =
        findOne { Member.id eq id }

    fun findOne(userId: String): MemberDto? =
        findOne { Member.userId eq userId }

    fun create(dto: MemberDto): MemberDto? =
        transaction {
            val existing = Member.select {
                (Member.userId eq dto.userId) and (Member.deleted eq false)
            }.firstOrNull()

            if (existing != null)
                null
            else
                Member.insertAndGetId {
                    it[userId] = dto.userId
                    it[name] = dto.name
                    it[password] = dto.password.hash()
                    it[memberInformationId] = null
                    it[accountNonExpired] = true
                    it[accountNonLocked] = true
                    it[credentialsNonExpired] = true
                    it[enabled] = true
                    it[createdBy] = dto.userId
                    it[createdDate] = LocalDateTime.now()
                    it[lastModifiedBy] = dto.userId
                    it[lastModifiedDate] = LocalDateTime.now()
                    it[deleted] = false
                }.let {
                    Member.select {
                        (Member.id eq it.value) and (Member.deleted eq false)
                    }.firstOrNull()
                        ?.let(Member::toDto)
                        ?.also { member ->
                            MemberRole.insert {
                                it[memberId] = member.id
                                it[role] = Role.USER.value
                                it[createdBy] = dto.userId
                                it[createdDate] = LocalDateTime.now()
                                it[lastModifiedBy] = dto.userId
                                it[lastModifiedDate] = LocalDateTime.now()
                                it[deleted] = false
                            }
                        }
                }
        }

    fun update(dto: MemberDto): Int =
        transaction {
            Member.update({ Member.id eq dto.id }) {
                it[name] = dto.name
                it[userId] = dto.userId
                if (dto.newPassword != null) {
                    it[password] = dto.newPassword.hash()
                }
                if (dto.deleted != null) {
                    it[deleted] = dto.deleted
                }
            }
        }

    private fun findOne(op: SqlExpressionBuilder.() -> Op<Boolean>): MemberDto? = transaction {
        Member.select {
            op(this) and (Member.deleted eq false)
        }.firstOrNull()
            ?.let(Member::toDto)
    }
}