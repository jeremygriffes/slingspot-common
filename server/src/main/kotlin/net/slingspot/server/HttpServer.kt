package net.slingspot.server

/**
 * Basic interface for server implementations.
 */
public interface HttpServer {
    public val httpPort: Int?
    public val httpsPort: Int?
    public val endpoints: List<Endpoint>
    public val errors: List<Endpoint.Error>

    /**
     * Starts the server. Throws IllegalStateException if already started.
     */
    @Throws(IllegalStateException::class)
    public fun start(config: Config)

    public fun stop()

    public companion object {
        /**
         * Any implementation of HttpServer should reject these vulnerable cipher suites for SSL/TSL handshakes.
         */
        public val excludedCipherSuites: Array<String> = arrayOf(
            "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384",
            "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
            "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256",
            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
            "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256"
        )
    }
}
