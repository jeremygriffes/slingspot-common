package net.slingspot.website.sample

import net.slingspot.server.commandline.parse

fun main(vararg args: String) {
    parse(*args,
        content = "public",
        roleProvider = Roles(),
        start = { http, https, config ->
            AppServer(http, https).start(config)
        }
    )
}
