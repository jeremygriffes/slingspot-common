package net.slingspot.server.javalin

import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.http.Handler
import net.slingspot.server.Endpoint
import net.slingspot.server.Request
import net.slingspot.server.Response
import net.slingspot.server.javalin.auth.toJavalinRoles

internal fun Endpoint.toJavalin(javalin: Javalin) {
    when (method) {
        Endpoint.Method.Get -> javalin.get(path, toHandler(), access.toJavalinRoles())
        Endpoint.Method.Put -> javalin.put(path, toHandler(), access.toJavalinRoles())
        Endpoint.Method.Post -> javalin.post(path, toHandler(), access.toJavalinRoles())
        Endpoint.Method.Delete -> javalin.delete(path, toHandler(), access.toJavalinRoles())
    }
}

internal fun Endpoint.Error.toJavalin(javalin: Javalin) {
    javalin.error(statusCode, toHandler())
}

internal fun Endpoint.toHandler() = Handler {
    process(request(it), response(it))
}

internal fun Endpoint.Error.toHandler() = Handler {
    process(request(it), response(it))
}

internal fun request(context: Context) = object : Request {
    override val method: Endpoint.Method
        get() = Endpoint.Method.from(context.method())
    override val path: String
        get() = context.path()
    override val remoteAddress: String
        get() = context.ip()
    override val body: String
        get() = context.body()
    override val attributes: Map<String, Any?>
        get() = TODO("Not yet implemented")
    override val headers: Map<String, String>
        get() = TODO("Not yet implemented")

    override fun <T : Any> body(): T {
        TODO("Not yet implemented")
    }

    override fun bodyAsBytes(): ByteArray {
        TODO("Not yet implemented")
    }

    override fun <T> bodyAsClass(clazz: Class<T>): T {
        TODO("Not yet implemented")
    }

    override fun pathParam(key: String): String {
        TODO("Not yet implemented")
    }

    override fun basicAuthCredentials(): String {
        TODO("Not yet implemented")
    }
}

internal fun response(context: Context) = object : Response {
    override var statusCode: Int? = null

    override fun html(html: String) {
        context.html(html)
    }

    override fun json(json: Any) {
        context.json(json)
    }

    override fun render(filePath: String, model: Map<String, Any?>) {
        context.render(filePath, model)
    }
}