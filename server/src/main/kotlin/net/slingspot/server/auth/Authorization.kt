package net.slingspot.server.auth

import net.slingspot.server.auth.Authorization.Companion.Headers.BEARER
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
    public fun isAuthorized(token: String?, permittedRoles: Set<UserRole>): Boolean

    public companion object {
        public val PUBLIC: List<UserRole> = emptyList()

        public fun bearer(auth: String?): String? = auth?.let {
            if (it.startsWith(BEARER)) it.removePrefix(BEARER) else null
        }?.trim()

        public object Headers {
            public const val AUTHORIZATION: String = "Authorization"
            public const val BEARER: String = "Bearer"
        }
    }
}
