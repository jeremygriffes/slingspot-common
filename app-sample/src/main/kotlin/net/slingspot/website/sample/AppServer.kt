package net.slingspot.website.sample

import net.slingspot.server.javalin.ServerImpl
import net.slingspot.website.sample.endpoint.ExampleEndpoint
import net.slingspot.website.sample.error.ErrorNotFound

class AppServer(
    override val httpPort: Int? = 8080,
    override val httpsPort: Int? = 8443
) : ServerImpl() {
    override val endpoints = listOf(
        ExampleEndpoint.page
    )

    override val errors = listOf(
        ErrorNotFound.page
    )
}
