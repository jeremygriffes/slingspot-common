package net.slingspot.server.javalin

import io.javalin.Javalin
import io.mockk.mockk
import net.slingspot.log.ConsoleLogger
import net.slingspot.log.Log
import net.slingspot.log.Logger
import net.slingspot.server.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

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

    private fun getSimpleConfig() = Config(
        Environment.Development, "path", "PKCS12", "password", null,
        object : RoleProvider {
            override val allRoles = setOf<UserRole>()

            override fun isAuthorized(request: Request, endpointRoles: Set<UserRole>): Boolean {
                return true
            }
        }
    )

    @Test
    fun `start server throws when already running`() {
        val server = getSimpleServer()
        val config = getSimpleConfig()

        server.start(config)

        assertThrows<IllegalStateException> { server.start(config) }
    }

    @Test
    fun `server can restart after stopping`() {
        val server = getSimpleServer()
        val config = getSimpleConfig()

        server.start(config)

        server.stop()

        assertDoesNotThrow { server.start(config) }
    }

    @Test
    fun `server stop is idempotent`() {
        val server = getSimpleServer()
        val config = getSimpleConfig()

        server.start(config)

        server.stop()

        server.stop()
    }
}
