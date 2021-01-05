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
    private val jwtParser = Jwts.parserBuilder()
        .setAllowedClockSkewSeconds(CLOCK_SKEW_IN_SECONDS)
        .setSigningKey(public)
        .build()

    override val allRoles = allRoles
    override val publicKey = public
    override val privateKey = private

    override fun isAuthorized(token: String?, permittedRoles: Collection<UserRole>) =
        permittedRoles == PUBLIC || token != null && try {
            jwtParser.parseClaimsJws(token).body?.run {
                issuer == CLAIM_ISSUER &&
                        get(CLAIM_ROLES, String::class.java)
                            ?.decode()
                            ?.containsAll(permittedRoles.map { it.title })
                        ?: false
            } ?: false
        } catch (e: JwtException) {
            Log.i(tag) { "JWT rejected" }
            false
        }
}
