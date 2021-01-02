package net.slingspot.server.auth

import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

public interface Authorization {
    public val allRoles: Set<UserRole>
    public val publicKey: RSAPublicKey
    public val privateKey: RSAPrivateKey?
}

public interface UserRole {
    public val title: String

    public companion object {
        public val EVERYONE: List<UserRole> = listOf()
    }
}
