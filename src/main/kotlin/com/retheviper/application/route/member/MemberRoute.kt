package com.retheviper.application.route.member

import com.retheviper.common.constant.Constants
import com.retheviper.common.extension.hash
import com.retheviper.common.extension.verifyWith
import com.retheviper.common.role.Role
import com.retheviper.domain.dto.MemberDto
import com.retheviper.infrastructure.repository.member.MemberRepository
import com.retheviper.application.route.member.model.request.MemberUpsertForm
import com.retheviper.application.route.member.model.response.MemberResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*

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
        val result = MemberRepository.create(body.toDto(setOf(Role.USER)))

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
        val (parameter, existing) = getRequestAndExisting()

        if (!verify(parameter, existing)) {
            return@put
        }

        val result =
            if (parameter.userId != checkNotNull(existing).userId) {
                val conflict = MemberRepository.findOne(parameter.userId)

                if (conflict != null) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = "${parameter.userId} already exists"
                    )
                    return@put
                }

                MemberRepository.update(parameter)
            } else {
                MemberRepository.update(parameter)
            }

        when (result) {
            1 -> call.respond(
                MemberResponse(
                    id = checkNotNull(parameter.id),
                    name = parameter.name,
                    userId = parameter.userId
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
        val (parameter, existing) = getRequestAndExisting()

        if (!verify(parameter, existing) || parameter.userId != existing?.userId) {
            return@delete
        }

        when (MemberRepository.update(existing.copy(deleted = true))) {
            1 -> call.respond(
                status = HttpStatusCode.OK,
                message = "${parameter.id} successfully deleted"
            )

            0 -> call.respond(
                status = HttpStatusCode.InternalServerError,
                message = "delete failed"
            )
        }
    }
}

private suspend fun PipelineContext<*, ApplicationCall>.getRequestAndExisting(): Pair<MemberDto, MemberDto?> {
    val id = requireNotNull(call.parameters[Constants.ID]).toInt()
    val body = call.receive<MemberUpsertForm>()
    val existing = MemberRepository.findOne(id)

    return body.toDto(id) to existing
}

private suspend fun PipelineContext<*, ApplicationCall>.verify(parameter: MemberDto, existing: MemberDto?) =
    when {
        existing == null -> {
            call.respond(
                status = HttpStatusCode.NotFound,
                message = "${parameter.id} not exists"
            )
            false
        }

        !parameter.password.verifyWith(existing.password) -> {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = "password not matches"
            )
            false
        }

        else -> true
    }