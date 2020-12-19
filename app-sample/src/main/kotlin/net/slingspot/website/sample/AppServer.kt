package net.slingspot.website.sample

import net.slingspot.server.javalin.ServerImpl
import net.slingspot.website.sample.endpoint.Index
import net.slingspot.website.sample.error.ErrorNotFound

class AppServer(
    override val httpPort: Int? = 80,
    override val httpsPort: Int? = 443
) : ServerImpl() {
    override val endpoints = listOf(
        Index.getPage
    )

    override val errors = listOf(
        ErrorNotFound.page
    )
}
