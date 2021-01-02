package net.slingspot.server

/**
 * Identifies the path, type, and password of the SSL/TLS certificate.
 */
public data class CertKeystore(
    val path: String,
    val type: String,
    val password: String
)
