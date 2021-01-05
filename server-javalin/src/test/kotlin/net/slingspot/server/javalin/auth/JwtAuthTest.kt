package net.slingspot.server.javalin.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import net.slingspot.log.ConsoleLogger
import net.slingspot.log.Log
import net.slingspot.log.Logger
import net.slingspot.server.auth.Authorization
import net.slingspot.server.auth.Authorization.Companion.CLAIM_ISSUER
import net.slingspot.server.auth.Authorization.Companion.CLAIM_ROLES
import net.slingspot.server.auth.Authorization.Companion.CLOCK_SKEW_IN_SECONDS
import net.slingspot.server.auth.Authorization.Companion.PUBLIC
import net.slingspot.server.auth.Authorization.Companion.encode
import net.slingspot.server.auth.UserRole
import net.slingspot.server.javalin.auth.JwtAuthTest.TestRole.*
import net.slingspot.server.javalin.auth.JwtAuthTest.TestRole.Companion.allRoles
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.security.PrivateKey
import java.time.Instant
import java.time.temporal.ChronoUnit.SECONDS
import java.util.*

class JwtAuthTest {
    init {
        Log.loggers = listOf(ConsoleLogger(Logger.Level.VERBOSE))
    }

    private val key = keyPair()

    private fun keyPair() = Keys.keyPairFor(SignatureAlgorithm.RS512)

    private fun buildJwt(
        issuer: String = CLAIM_ISSUER,
        issuedAt: Instant = Instant.now().truncatedTo(SECONDS),
        expiration: Instant = issuedAt.plus(10, SECONDS),
        userRoles: Set<UserRole>,
        privateKey: PrivateKey
    ) = Jwts.builder()
        .setSubject("test")
        .setIssuer(issuer)
        .setIssuedAt(Date.from(issuedAt))
        .setExpiration(Date.from(expiration))
        .addClaims(mapOf(CLAIM_ROLES to userRoles.encode()))
        .signWith(privateKey)
        .compact()

    @Test
    fun `publicly accessible resource requires no auth token`() {
        val authorizer = Authorization.jwt(key.public, null, allRoles)
        val endpointRoles = PUBLIC

        assertTrue(authorizer.isAuthorized(null, endpointRoles))
    }

    @Test
    fun `auth succeeds when user role matches endpoint role`() {
        val authorizer = Authorization.jwt(key.public, null, allRoles)
        val userRoles = setOf(RoleUser)
        val endpointRoles = setOf(RoleUser)
        val jwt = buildJwt(userRoles = userRoles, privateKey = key.private)

        assertTrue(authorizer.isAuthorized(jwt, endpointRoles))
    }

    @Test
    fun `auth fails when user role does not match endpoint role`() {
        val authorizer = Authorization.jwt(key.public, null, allRoles)
        val userRoles = setOf(RoleUser)
        val endpointRoles = setOf(RoleAdmin)
        val jwt = buildJwt(userRoles = userRoles, privateKey = key.private)

        assertFalse(authorizer.isAuthorized(jwt, endpointRoles))
    }

    @Test
    fun `user has multiple required roles for endpoint`() {
        val authorizer = Authorization.jwt(key.public, null, allRoles)
        val userRoles = setOf(RoleUser, RoleAdmin, RoleSystem)
        val endpointRoles = setOf(RoleSystem, RoleAdmin)
        val jwt = buildJwt(userRoles = userRoles, privateKey = key.private)

        assertTrue(authorizer.isAuthorized(jwt, endpointRoles))
    }

    @Test
    fun `user missing a required role for endpoint`() {
        val authorizer = Authorization.jwt(key.public, null, allRoles)
        val userRoles = setOf(RoleUser, RoleAdmin)
        val endpointRoles = setOf(RoleSystem, RoleAdmin)
        val jwt = buildJwt(userRoles = userRoles, privateKey = key.private)

        assertFalse(authorizer.isAuthorized(jwt, endpointRoles))
    }

    @Test
    fun `user has wrong key`() {
        val signingKey = keyPair().private
        val authorizer = Authorization.jwt(key.public, null, allRoles)
        val userRoles = setOf(RoleUser, RoleAdmin, RoleSystem)
        val endpointRoles = setOf(RoleSystem, RoleAdmin)
        val jwt = buildJwt(userRoles = userRoles, privateKey = signingKey)

        assertFalse(authorizer.isAuthorized(jwt, endpointRoles))
    }

    @Test
    fun `expired jwt is rejected`() {
        val authorizer = Authorization.jwt(key.public, null, allRoles)
        val userRoles = setOf(RoleUser, RoleAdmin, RoleSystem)
        val endpointRoles = setOf(RoleSystem, RoleAdmin)

        val jwt = buildJwt(
            userRoles = userRoles,
            privateKey = key.private,
            issuedAt = Instant.parse("2021-01-01T00:00:00Z")
        )

        assertFalse(authorizer.isAuthorized(jwt, endpointRoles))
    }

    @Test
    fun `expired jwt is within clock skew is accepted`() {
        val authorizer = Authorization.jwt(key.public, null, allRoles)
        val userRoles = setOf(RoleUser, RoleAdmin, RoleSystem)
        val endpointRoles = setOf(RoleSystem, RoleAdmin)

        val jwt = buildJwt(
            userRoles = userRoles,
            privateKey = key.private,
            issuedAt = Instant.now().minus(CLOCK_SKEW_IN_SECONDS * 2, SECONDS),
            expiration = Instant.now().minus((CLOCK_SKEW_IN_SECONDS * 0.5f).toLong(), SECONDS)
        )

        assertTrue(authorizer.isAuthorized(jwt, endpointRoles))
    }

    private sealed class TestRole : UserRole {
        object RoleUser : TestRole() {
            override val title = "User"
        }

        object RoleAdmin : TestRole() {
            override val title = "Admin"
        }

        object RoleSystem : TestRole() {
            override val title = "System"
        }

        companion object {
            val allRoles = setOf(RoleUser, RoleAdmin)
        }
    }
}
