package net.slingspot.website.sample.error

import net.slingspot.server.Endpoint
import net.slingspot.server.Request
import net.slingspot.server.Response

class ErrorNotFound {
    companion object {
        val page = object : Endpoint.Error {
            override val statusCode = 404

            override fun process(request: Request, response: Response) {
                response.render("public/notFound.html")
            }
        }
    }
}
