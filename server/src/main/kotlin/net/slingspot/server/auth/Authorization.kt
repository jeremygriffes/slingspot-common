package net.slingspot.server.auth

import net.slingspot.server.auth.Authorization.Companion.Headers.BEARER
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.PrivateKey
import java.security.PublicKey

/**
 * Authorization designed for use with JWT and role-based authorization.
 */
public interface Authorization {
    public val allRoles: Set<UserRole>
    public val publicKey: PublicKey
    public val privateKey: PrivateKey?

    /**
     * Given a token and the permitted roles of the endpoint, return whether access is granted to the resources
     * therein. Unless otherwise specified, *all* roles defined by the endpoint *must* be present in the user's claimed
     * roles.
     *
     * By convention, the endpoint will be considered publicly accessible, without restriction, if the permitted role
     * set is empty.
     */
    public fun isAuthorized(token: String?, permittedRoles: Collection<UserRole>): Boolean

    public companion object {
        public val PUBLIC: Collection<UserRole> = emptySet()
        public const val CLAIM_ISSUER: String = "https://slingspot.net"
        public const val CLAIM_ROLES: String = "https://slingspot.net/jwt_claims/roles"
        public const val CLOCK_SKEW_IN_SECONDS: Long = 2L * 60

        public fun bearer(auth: String?): String? = auth?.let {
            if (it.startsWith(BEARER)) it.removePrefix(BEARER) else null
        }?.trim()

        public object Headers {
            public const val AUTHORIZATION: String = "Authorization"
            public const val BEARER: String = "Bearer"
        }

        public fun Collection<UserRole>.encode(): String = joinToString(",") {
            URLEncoder.encode(it.title, "utf-8")
        }

        public fun String.decode(): Set<String> = split(",").map { URLDecoder.decode(it, "utf-8") }.toSet()
    }
}
