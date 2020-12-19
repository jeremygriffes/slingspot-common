package net.slingspot.website.sample.endpoint

import net.slingspot.server.Endpoint
import net.slingspot.server.Request
import net.slingspot.server.Response

class Index {
    companion object {
        val getPage = object : Endpoint {
            override val method = Endpoint.Method.Get
            override val path = "/"
            override fun process(request: Request, response: Response) {
                response.render("root/index.jte")
            }
        }
    }
}
