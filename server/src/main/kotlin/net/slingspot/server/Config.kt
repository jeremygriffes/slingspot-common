package net.slingspot.server

import net.slingspot.server.auth.Authorization

public data class Config(
    val environment: Environment,
    val keystorePath: String,
    val keystoreType: String,
    val keystorePassword: String,
    val webContentClasspath: String?,
    val authorization: Authorization,
)
