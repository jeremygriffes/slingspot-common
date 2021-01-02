package net.slingspot.server.javalin

import io.javalin.Javalin
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import io.mockk.mockk
import net.slingspot.log.ConsoleLogger
import net.slingspot.log.Log
import net.slingspot.log.Logger
import net.slingspot.server.Config
import net.slingspot.server.Endpoint
import net.slingspot.server.Request
import net.slingspot.server.Response
import net.slingspot.server.auth.UserRole
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.io.File

class ServerImplTest {
    private val mockJavalin = mockk<Javalin>(relaxed = true)

    init {
        Log.loggers = listOf(ConsoleLogger(Logger.Level.VERBOSE))
    }

    private fun mockServer(
        endpoints: List<Endpoint>,
        errors: List<Endpoint.Error>
    ) = object : ServerImpl() {
        override val httpPort = 80
        override val httpsPort = 443
        override val endpoints = endpoints
        override val errors = errors
        override fun create(config: Config) = mockJavalin
    }

    private fun getSimpleServer() = mockServer(
        endpoints = listOf(
            object : Endpoint {
                override val method = Endpoint.Method.Get
                override val path = "test"
                override val access = listOf<UserRole>()
                override fun process(request: Request, response: Response) {
                    TODO("Not yet implemented")
                }
            }
        ), errors = listOf()
    )

    private fun ServerImpl.startMock() = start(mockk())

    @Test
    fun `start server throws when already running`() {
        val server = getSimpleServer()

        server.startMock()

        assertThrows<IllegalStateException> { server.startMock() }
    }

    @Test
    fun `server can restart after stopping`() {
        val server = getSimpleServer()

        server.startMock()

        server.stop()

        assertDoesNotThrow { server.startMock() }
    }

    @Test
    fun `server stop is idempotent`() {
        val server = getSimpleServer()

        server.startMock()

        server.stop()

        server.stop()
    }

    @Test
    @Disabled
    fun `generate key pair for jwt signing`() {
        // To quickly generate public/private keys, enable this test and run it.
        val keyPair = Keys.keyPairFor(SignatureAlgorithm.RS512)
        val privateBytes = keyPair.private.encoded
        File("authorization_private.key").writeBytes(privateBytes)

        val publicBytes = keyPair.public.encoded
        File("authorization_public.key").writeBytes(publicBytes)
    }
}
