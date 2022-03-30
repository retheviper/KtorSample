package com.retheviper.application.configuration.security

import com.retheviper.common.exception.AuthorizationException
import com.retheviper.common.role.Role
import com.retheviper.domain.dto.MemberPrincipal
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import mu.KotlinLogging

class RoleBasedAuthorization(config: Configuration) {
    private val logger = KotlinLogging.logger { }
    private val getRoles = config.roles

    class Configuration {
        var roles: (Principal) -> Set<Role> = { emptySet() }

        fun getRoles(gr: (Principal) -> Set<Role>) {
            roles = gr
        }
    }

    fun interceptPipeline(
        pipeline: ApplicationCallPipeline,
        any: Set<Role>? = null,
        all: Set<Role>? = null,
        none: Set<Role>? = null
    ) {
        pipeline.insertPhaseAfter(ApplicationCallPipeline.Features, Authentication.ChallengePhase)
        pipeline.insertPhaseAfter(Authentication.ChallengePhase, AuthorizationPhase)
        pipeline.intercept(AuthorizationPhase) {
            val principal = call.authentication.principal<MemberPrincipal>()
                ?: throw AuthorizationException("Missing principal")
            val roles = getRoles(principal)
            val denyReasons = mutableListOf<String>()
            all?.let {
                val missing = all - roles
                if (missing.isNotEmpty()) {
                    denyReasons += "User ${principal.username} lacks required role(s) ${missing.joinToString(" and ")}"
                }
            }
            any?.let {
                if (any.none { it in roles }) {
                    denyReasons +=
                        "User ${principal.username} has none of the sufficient role(s) ${
                            any.joinToString(" or ")
                        }"
                }
            }
            none?.let {
                if (none.any { it in roles }) {
                    denyReasons += "User ${principal.username} has forbidden role(s) ${
                        (none.intersect(roles)).joinToString(" and ")
                    }"
                }
            }
            if (denyReasons.isNotEmpty()) {
                val message = denyReasons.joinToString(". ")
                logger.warn { "Authorization failed for ${call.request.path()}. $message" }
                throw AuthorizationException(message)
            }
        }
    }


    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, RoleBasedAuthorization> {
        override val key = AttributeKey<RoleBasedAuthorization>("RoleBasedAuthorization")
        val AuthorizationPhase = PipelinePhase("Authorization")
        override fun install(
            pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit
        ): RoleBasedAuthorization {
            val configuration = Configuration().apply(configure)
            return RoleBasedAuthorization(configuration)
        }
    }
}

class AuthorizedRouteSelector(private val description: String) : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int) = RouteSelectorEvaluation.Constant
    override fun toString(): String = "(authorize ${description})"
}