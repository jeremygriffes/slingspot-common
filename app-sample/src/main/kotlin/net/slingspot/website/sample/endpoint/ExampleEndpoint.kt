package net.slingspot.website.sample.endpoint

import net.slingspot.server.Endpoint
import net.slingspot.server.Request
import net.slingspot.server.Response

class ExampleEndpoint {
    data class ExampleData(val message: String)

    companion object {
        val page = object : Endpoint {
            override val method = Endpoint.Method.Get
            override val path = "/example"
            override fun process(request: Request, response: Response) {
                response.json(ExampleData("Hello, ${request.remoteAddress}"))
            }
        }
    }
}
