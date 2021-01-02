package net.slingspot.server.javalin

import io.javalin.core.security.Role
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import net.slingspot.log.Log
import net.slingspot.server.auth.UserRole.Companion.PUBLIC
import net.slingspot.server.javalin.auth.JavalinRole
import java.security.PublicKey

internal object JwtAuth {

    fun isAuthorized(key: PublicKey, bearer: String?, permittedRoles: Set<Role>): Boolean {
        if (permittedRoles == PUBLIC) return true

        val token = bearer?.removePrefix(HEADER_VAL_BEARER)?.trim() ?: return false

        try {
            val claimedRoles = Jwts.parserBuilder()
                .setAllowedClockSkewSeconds(CLOCK_SKEW_SECONDS)
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body?.get(CLAIM_ROLES, String::class.java)
                ?.split(',')
                ?: return false

            val requiredEndpointRoles = permittedRoles.map { (it as JavalinRole).userRole.title }

            // For now, *all* roles defined by the endpoint *must* be present in the user's claimed roles.
            // This logic might be revisited, or perhaps role-based authorization may be replaced by attribute-based.
            return claimedRoles.containsAll(requiredEndpointRoles)
        } catch (e: JwtException) {
            Log.i(tag) { "JWT rejected" }
            return false
        }
    }

    private const val tag = "JwtAuth"
    private const val CLOCK_SKEW_SECONDS = 2L * 60
    private const val HEADER_VAL_BEARER = "Bearer"
    private const val CLAIM_ROLES = "roles"
}
