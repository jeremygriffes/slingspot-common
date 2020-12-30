package net.slingspot.server

public data class Config(
    val environment: Environment,
    val keystorePath: String,
    val keystoreType: String,
    val keystorePassword: String,
    val webContentClasspath: String?,
    val roleProvider: RoleProvider,
)
