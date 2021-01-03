package net.slingspot.server.javalin.auth

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import net.slingspot.log.Log
import net.slingspot.server.auth.Authorization
import net.slingspot.server.auth.UserRole
import java.security.PrivateKey
import java.security.PublicKey

public fun Authorization.Companion.jwt(
    public: PublicKey,
    private: PrivateKey?,
    allRoles: Set<UserRole>
): Authorization = object : Authorization {
    private val tag = "AuthorizationJwt"
    private val clockSkewInSeconds = 2L * 60
    private val claimRoles = "https://slingspot.net/jwt_claims/roles"
    private val jwtParser = Jwts.parserBuilder()
        .setAllowedClockSkewSeconds(clockSkewInSeconds)
        .setSigningKey(public)
        .build()

    override val allRoles = allRoles
    override val publicKey = public
    override val privateKey = private

    override fun isAuthorized(token: String?, permittedRoles: Set<UserRole>) =
        permittedRoles == PUBLIC || token != null && try {
            jwtParser.parseClaimsJws(token).body
                ?.get(claimRoles, String::class.java)
                ?.split(',')
                ?.containsAll(permittedRoles.map { it.title })
                ?: false
        } catch (e: JwtException) {
            Log.i(tag) { "JWT rejected" }
            false
        }
}
