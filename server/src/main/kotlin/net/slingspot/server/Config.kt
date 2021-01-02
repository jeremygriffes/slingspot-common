package net.slingspot.server

import net.slingspot.server.auth.Authorization

public data class Config(
    val environment: Environment,
    val certKeystore: CertKeystore,
    val webContentClasspath: String?,
    val authorization: Authorization,
)
