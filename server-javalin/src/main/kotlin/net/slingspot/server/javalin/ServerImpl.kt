package net.slingspot.server.javalin

import io.javalin.Javalin
import net.slingspot.lang.arrayOfNotNull
import net.slingspot.log.Log
import net.slingspot.server.Config
import net.slingspot.server.HttpServer
import org.eclipse.jetty.server.*
import org.eclipse.jetty.util.ssl.SslContextFactory

/**
 * Javalin implementation of the HttpServer interface.
 */
public abstract class ServerImpl : HttpServer {
    private val tag = ServerImpl::class.java.simpleName
    private val lock = Any()
    private var instance: Javalin? = null

    private val connectionFactory = HttpConnectionFactory(HttpConfiguration().apply {
        addCustomizer(SecureRequestCustomizer())
        addCustomizer(ForwardedRequestCustomizer())
    })

    private fun sslContextFactory(
        keystorePath: String,
        keystoreType: String,
        keystorePassword: String
    ) = SslContextFactory.Server().apply {
        keyStoreType = keystoreType
        keyStorePath = keystorePath
        setKeyStorePassword(keystorePassword)
        setKeyManagerPassword(keystorePassword)
        setExcludeCipherSuites(
            *excludeCipherSuites,
            *HttpServer.excludedCipherSuites
        )
    }

    /**
     * Configures the Javalin server to listen for http and https, if the respective ports have been provided.
     */
    internal open fun create(config: Config) = Javalin.create { javalin ->
        javalin.enforceSsl = true

        config.webContentClasspath?.let { javalin.addStaticFiles(it) }

        javalin.accessManager { handler, ctx, permittedRoles ->
            if (JwtAuth.isAuthorized(
                    config.authorization.publicKey,
                    ctx.header(HEADER_AUTHORIZATION),
                    permittedRoles
                )
            ) {
                handler.handle(ctx)
            } else {
                ctx.status(401).result("Unauthorized")
            }
        }

        javalin.server {
            Server().apply {
                connectors = arrayOfNotNull(
                    httpPort?.let {
                        ServerConnector(this, connectionFactory).apply { port = it }
                    },
                    httpsPort?.let {
                        ServerConnector(
                            this,
                            sslContextFactory(config.keystorePath, config.keystoreType, config.keystorePassword),
                            connectionFactory
                        ).apply { port = it }
                    }
                )
            }
        }
    }

    override fun start(config: Config): Unit = synchronized(lock) {
        if (instance != null) {
            throw IllegalStateException("Server is already running")
        }

        create(config).apply {
            exception(Exception::class.java) { e, _ -> Log.e(tag, e) { "Caught exception: ${e.message}" } }
            start()
            routes {
                before {
                    Log.i(tag) { "Received: ${it.req.remoteAddr} requested ${it.req.requestURL}" }
                }
                endpoints.forEach { it.toJavalin(this) }
                errors.forEach { it.toJavalin(this) }
            }
            instance = this
        }
    }

    override fun stop(): Unit = synchronized(lock) {
        if (instance == null) {
            Log.w(tag) { "Ignoring request to stop server that is not running" }
        }
        instance?.stop()
        instance = null
    }

    internal companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
    }
}
