package com.retheviper.route.member

import com.retheviper.common.constant.Constants
import com.retheviper.domain.table.Member
import com.retheviper.route.member.model.request.MemberUpsertForm
import com.retheviper.route.member.model.response.MemberResponse
import com.toxicbakery.bcrypt.Bcrypt
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

fun Route.members() {

    val path = "${Constants.API_BASE_PATH}/members"

    val saltRound = 5

    /**
     * List Members
     */
    get(path) {
        val result = transaction {
            Member.selectAll().map(Member::toDto)
        }

        call.respond(result.map(MemberResponse::from))
    }

    /**
     * Get Member
     */
    get("${path}/{id}") {
        val id = requireNotNull(call.parameters[Constants.ID]).toInt()

        val result = transaction {
            Member.select {
                (Member.id eq id) and (Member.deleted eq false)
            }.firstOrNull()
                ?.let(Member::toDto)
        }

        result?.let {
            call.respond(MemberResponse.from(it))
        } ?: call.respond(
            status = HttpStatusCode.NotFound,
            message = "$id not found"
        )
    }

    /**
     * Create Member
     */
    post(path) {
        val body = call.receive<MemberUpsertForm>()

        val result = transaction {
            val existing = Member.select {
                (Member.userId eq body.userId)
            }.firstOrNull()

            if (existing != null)
                null
            else
                Member.insertAndGetId {
                    it[userId] = body.userId
                    it[name] = body.name
                    it[password] = Bcrypt.hash(body.password, saltRound).toString()
                    it[memberInformationId] = null
                    it[accountNonExpired] = true
                    it[accountNonLocked] = true
                    it[credentialsNonExpired] = true
                    it[enabled] = true
                    it[createdBy] = body.userId
                    it[createdDate] = LocalDateTime.now()
                    it[lastModifiedBy] = body.userId
                    it[lastModifiedDate] = LocalDateTime.now()
                    it[deleted] = false
                }.let {
                    Member.select {
                        (Member.id eq it.value) and (Member.deleted eq false)
                    }.firstOrNull()
                        ?.let(Member::toDto)
                }
        }

        result?.let {
            call.respond(MemberResponse.from(it))
        } ?: call.respond(
            status = HttpStatusCode.Conflict,
            message = "${body.userId} already exists"
        )
    }

    /**
     * Update Member
     */
    put("${path}/{id}") {
        val id = requireNotNull(call.parameters[Constants.ID]).toInt()
        val body = call.receive<MemberUpsertForm>()
        val existing = transaction {
            Member.select {
                (Member.id eq id) and (Member.deleted eq false)
            }.firstOrNull()
                ?.let(Member::toDto)
        }

        if (existing == null) {
            call.respond(
                status = HttpStatusCode.NotFound,
                message = "${body.userId} not exists"
            )
            return@put
        }

        if (Bcrypt.verify(body.password, existing.password.toByteArray())) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = "password not matches"
            )
            return@put
        }

        val update = { userId: String?, newPassword: String? ->
            transaction {
                Member.update({ Member.id eq existing.id }) {
                    it[name] = body.name
                    if (userId != null) {
                        it[Member.userId] = body.userId
                    }
                    if (newPassword != null) {
                        it[password] = Bcrypt.hash(newPassword, saltRound).toString()
                    }
                }
            }
        }

        val result =
            if (body.userId != existing.userId) {
                val conflict = transaction {
                    Member.select {
                        (Member.userId eq body.userId) and (Member.deleted eq false)
                    }.firstOrNull()
                        ?.let(Member::toDto)
                }

                if (conflict != null) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = "${body.userId} already exists"
                    )
                    return@put
                }

                update(body.userId, body.newPassword)
            } else {
                update(null, body.newPassword)
            }

        when (result) {
            1 -> call.respond(
                MemberResponse(
                    id = id,
                    name = body.name,
                    userId = body.userId
                )
            )

            0 -> call.respond(
                status = HttpStatusCode.InternalServerError,
                message = "Update failed"
            )
        }
    }

    /**
     * Delete Member
     */
    delete("${path}/{id}") {
        val id = requireNotNull(call.parameters[Constants.ID]).toInt()
        val password = call.receive<String>()
        val existing = transaction {
            Member.select {
                (Member.id eq id) and (Member.deleted eq false)
            }.firstOrNull()
                ?.let(Member::toDto)
        }

        if (existing == null) {
            call.respond(
                status = HttpStatusCode.NotFound,
                message = "$id not exists"
            )
            return@delete
        }

        if (Bcrypt.verify(password, existing.password.toByteArray())) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = "password not matches"
            )
            return@delete
        }

        val result = transaction {
            Member.update({ (Member.id eq id) and (Member.deleted eq false) }) {
                it[deleted] = true
            }
        }

        when (result) {
            1 -> call.respond(
                status = HttpStatusCode.OK,
                message = "$id successfully deleted"
            )

            0 -> call.respond(
                status = HttpStatusCode.InternalServerError,
                message = "delete failed"
            )
        }
    }
}