package net.slingspot.website.sample

import net.slingspot.server.Content
import net.slingspot.server.commandline.parse
import java.nio.file.Path

fun main(vararg args: String) {
    parse(*args,
        content = Content(
            Path.of("public"),
            Path.of("app-sample", "src", "main", "jte"),
            Path.of("jte-classes")
        ),
        start = { http, https, config ->
            AppServer(http, https).start(config)
        }
    )
}
