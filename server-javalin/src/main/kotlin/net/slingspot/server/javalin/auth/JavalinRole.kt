package net.slingspot.server.javalin.auth

import io.javalin.core.security.Role
import net.slingspot.server.auth.UserRole

/**
 * Internal Javalin-based Role that wraps the generic slingspot server UserRole.
 */
internal data class JavalinRole(val userRole: UserRole) : Role

internal fun Collection<UserRole>.toJavalinRoles() = map { JavalinRole(it) }.toSet()

internal fun Collection<Role>.toUserRoles() = map { (it as JavalinRole).userRole }.toSet()
