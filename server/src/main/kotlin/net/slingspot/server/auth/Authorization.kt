package net.slingspot.server.auth

import java.security.PrivateKey
import java.security.PublicKey

public interface Authorization {
    public val allRoles: Set<UserRole>
    public val publicKey: PublicKey
    public val privateKey: PrivateKey?

    public fun isAuthorized(token: String, roles: Set<UserRole>): Boolean {
        return true
    }
}

