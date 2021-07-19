package com.retheviper.route.member

import com.retheviper.common.constant.Constants
import com.retheviper.infrastructure.repository.member.MemberRepository
import com.retheviper.route.member.model.request.MemberUpsertForm
import com.retheviper.route.member.model.response.MemberResponse
import com.toxicbakery.bcrypt.Bcrypt
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.members() {

    val path = "${Constants.API_BASE_PATH}/members"

    /**
     * List Members
     */
    get(path) {
        val result = MemberRepository.findAll()
        call.respond(result.map(MemberResponse::from))
    }

    /**
     * Get Member
     */
    get("${path}/{id}") {
        val id = requireNotNull(call.parameters[Constants.ID]).toInt()

        val result = MemberRepository.findOne(id)

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

        val result = MemberRepository.create(body.toDto())

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
        val existing = MemberRepository.findOne(id)

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

        val result =
            if (body.userId != existing.userId) {
                val conflict = MemberRepository.findOne(body.userId)

                if (conflict != null) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = "${body.userId} already exists"
                    )
                    return@put
                }

                MemberRepository.update(body.toDto(id))
            } else {
                MemberRepository.update(body.toDto(id))
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
        val existing = MemberRepository.findOne(id)

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

        when (MemberRepository.update(existing.copy(deleted = true))) {
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