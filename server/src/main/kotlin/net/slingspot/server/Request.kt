package net.slingspot.server

import io.javalin.core.security.BasicAuthCredentials

public interface Request {
    public val method: Endpoint.Method
    public val path: String
    public val remoteAddress: String
    public val body: String
    public val attributes: Map<String, Any?>

    public fun <T : Any> body(): T
    public fun bodyAsBytes(): ByteArray
    public fun <T> bodyAsClass(clazz: Class<T>): T
    public fun pathParam(key: String): String
    public fun basicAuthCredentials(): BasicAuthCredentials

}