package com.retheviper.infrasturcture.repository

import com.retheviper.common.role.Role
import com.retheviper.domain.dto.MemberDto
import com.retheviper.infrastructure.repository.member.MemberRepository
import com.retheviper.plugins.configureDatabase
import com.retheviper.testbase.KtorTestBase
import io.kotest.core.test.TestCaseOrder
import io.kotest.matchers.shouldBe

class MemberRepositoryTest : KtorTestBase() {

    override fun testCaseOrder(): TestCaseOrder = TestCaseOrder.Sequential

    private val dto = MemberDto(
        userId = testUserId.substring(3),
        name = testName.substring(3),
        password = testPassword.substring(3),
        role = setOf(Role.USER)
    )

    private val updatedDto = MemberDto(
        userId = testUserId.reversed(),
        name = testName.reversed(),
        password = testPassword,
        newPassword = testPassword.reversed()
    )

    init {
        beforeSpec {
            configureDatabase()
        }

        "create" {
            val actual = checkNotNull(MemberRepository.create(dto))
                .also { id = checkNotNull(it.id) }
            actual.userId shouldBe dto.userId
            actual.name shouldBe dto.name
            actual.accountNonExpired shouldBe true
            actual.accountNonLocked shouldBe true
            actual.credentialsNonExpired shouldBe true
            actual.role shouldBe dto.role

        }

        "findOne(id)" {
            val actual = checkNotNull(MemberRepository.findOne(id))
            actual.userId shouldBe dto.userId
            actual.name shouldBe dto.name
            actual.accountNonExpired shouldBe true
            actual.accountNonLocked shouldBe true
            actual.credentialsNonExpired shouldBe true
            actual.role shouldBe dto.role
        }

        "update" {
            val actual = checkNotNull(MemberRepository.update(updatedDto.copy(id = id)))
            actual shouldBe 1
        }

        "findAll" {
            val result = MemberRepository.findAll()
            result.isEmpty() shouldBe false
            val actual = checkNotNull(result.firstOrNull { it.id == id })
            actual.userId shouldBe updatedDto.userId
            actual.name shouldBe updatedDto.name
            actual.accountNonExpired shouldBe true
            actual.accountNonLocked shouldBe true
            actual.credentialsNonExpired shouldBe true
            actual.role shouldBe dto.role
        }

        "delete" {
            val result = MemberRepository.update(updatedDto.copy(id = id, deleted = true))
            result shouldBe 1
        }

        "findOne(userId)" {
            val actual = MemberRepository.findOne(updatedDto.userId)
            actual shouldBe null
        }
    }
}