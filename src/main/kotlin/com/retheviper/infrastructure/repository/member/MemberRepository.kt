package com.retheviper.infrastructure.repository.member

import com.retheviper.common.extension.hash
import com.retheviper.common.role.Role
import com.retheviper.domain.dto.MemberDto
import com.retheviper.infrastructure.table.Member
import com.retheviper.infrastructure.table.MemberRole
import com.retheviper.infrastructure.table.MemberRole.role
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object MemberRepository {

    fun findAll(): List<MemberDto> =
        transaction { Member.selectAll().map(Member::toDto) }

    fun findOne(id: Int): MemberDto? =
        transaction { findOne { Member.id eq id } }

    fun findOne(userId: String): MemberDto? =
        transaction { findOne { Member.userId eq userId } }

    fun create(dto: MemberDto): MemberDto? =
        transaction {
            val existing = findOne { Member.userId eq dto.userId }

            if (existing != null) {
                null
            } else {
                val createdMember = Member.insertAndGetId {
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
                }.let { findOne { Member.id eq it.value } }

                if (createdMember != null) {
                    val createdMemberRole = dto.role.map { memberRole ->
                        MemberRole.insertAndGetId {
                            it[memberId] = createdMember.id
                            it[role] = memberRole.value
                            it[createdBy] = dto.userId
                            it[createdDate] = LocalDateTime.now()
                            it[lastModifiedBy] = dto.userId
                            it[lastModifiedDate] = LocalDateTime.now()
                            it[deleted] = false
                        }.let {
                            val result = MemberRole.select { (MemberRole.id) eq it.value }.first()
                                Role.valueOf(result[role])
                        }
                    }.toSet()

                    createdMember.copy(role = createdMemberRole)
                } else {
                    null
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

    /**
     * Query to find Member.
     * need [transaction].
     */
    internal fun findOne(op: SqlExpressionBuilder.() -> Op<Boolean>): MemberDto? =
        Member.select { op(this) and (Member.deleted eq false) }
            .firstOrNull()
            ?.let(Member::toDto)
}
