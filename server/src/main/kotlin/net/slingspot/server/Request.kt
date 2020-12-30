package net.slingspot.server

public interface Request {
    public val method: Endpoint.Method
    public val path: String
    public val remoteAddress: String
    public val body: String
    public val attributes: Map<String, Any?>
    public val headers: Map<String, String>

    public fun <T : Any> body(): T
    public fun bodyAsBytes(): ByteArray
    public fun <T> bodyAsClass(clazz: Class<T>): T
    public fun pathParam(key: String): String
    public fun basicAuthCredentials(): String
}
