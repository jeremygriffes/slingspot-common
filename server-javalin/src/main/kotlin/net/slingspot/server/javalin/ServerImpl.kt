package net.slingspot.server.javalin

import io.javalin.Javalin
import net.slingspot.lang.arrayOfNotNull
import net.slingspot.log.Log
import net.slingspot.server.CertKeystore
import net.slingspot.server.Config
import net.slingspot.server.HttpServer
import net.slingspot.server.auth.Authorization.Companion.Headers.AUTHORIZATION
import net.slingspot.server.auth.Authorization.Companion.bearer
import net.slingspot.server.javalin.auth.toUserRoles
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

    private fun sslContextFactory(certKeystore: CertKeystore) = SslContextFactory.Server().apply {
        keyStoreType = certKeystore.type
        keyStorePath = certKeystore.path
        setKeyStorePassword(certKeystore.password)
        setKeyManagerPassword(certKeystore.password)
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

        javalin.accessManager { handler, context, permittedRoles ->
            val token = bearer(context.header(AUTHORIZATION))

            if (config.authorization.isAuthorized(token, permittedRoles.toUserRoles())) {
                handler.handle(context)
            } else {
                context.status(401).result("Unauthorized")
            }
        }

        javalin.server {
            Server().apply {
                connectors = arrayOfNotNull(
                    httpPort?.let {
                        ServerConnector(this, connectionFactory).apply { port = it }
                    },
                    httpsPort?.let {
                        val sslContext = sslContextFactory(config.certKeystore)
                        ServerConnector(this, sslContext, connectionFactory).apply { port = it }
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
}
