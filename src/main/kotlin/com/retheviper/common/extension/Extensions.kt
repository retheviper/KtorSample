package com.retheviper.common.extension

import com.retheviper.application.configuration.security.AuthorizedRouteSelector
import com.retheviper.application.configuration.security.RoleBasedAuthorization
import com.retheviper.common.role.Role
import com.toxicbakery.bcrypt.Bcrypt
import io.ktor.application.*
import io.ktor.routing.*

/**
 * Hash string in UTF-8 encoding.
 */
fun String.hash(): String =
    Bcrypt.hash(this, 5).toString(Charsets.UTF_8)

/**
 * Verify string with another.
 * True means verified.
 */
fun String.verifyWith(encoded: String): Boolean =
    Bcrypt.verify(this, encoded.encodeToByteArray())

/**
 * Route with authority. Principal must have role.
 */
fun Route.withRole(role: Role, build: Route.() -> Unit): Route =
    authorizedRoute(all = setOf(role), build = build)

/**
 * Route with authority. Principal must have all roles.
 */
fun Route.withAllRoles(vararg roles: Role, build: Route.() -> Unit): Route =
    authorizedRoute(all = roles.toSet(), build = build)

/**
 * Route with authority. Principal must have any role.
 */
fun Route.withAnyRole(vararg roles: Role, build: Route.() -> Unit): Route =
    authorizedRoute(any = roles.toSet(), build = build)

/**
 * Route with authority. Principal must not have any roles.
 */
fun Route.withoutRoles(vararg roles: Role, build: Route.() -> Unit): Route =
    authorizedRoute(none = roles.toSet(), build = build)

/**
 * Route with roles.
 */
private fun Route.authorizedRoute(
    any: Set<Role>? = null,
    all: Set<Role>? = null,
    none: Set<Role>? = null,
    build: Route.() -> Unit
): Route {
    val description = listOfNotNull(
        any?.let { "anyOf (${any.joinToString(" ")})" },
        all?.let { "allOf (${all.joinToString(" ")})" },
        none?.let { "noneOf (${none.joinToString(" ")})" }
    ).joinToString(",")

    return createChild(AuthorizedRouteSelector(description)).apply {
        application.feature(RoleBasedAuthorization).interceptPipeline(this, any, all, none)
        build()
    }
}