package net.slingspot.website.sample

import net.slingspot.server.auth.UserRole
import net.slingspot.server.commandline.parse

fun main(vararg args: String) {
    parse(*args,
        publicResourceDirectory = "public",
        userRoles = userRoles,
        start = { http, https, config ->
            AppServer(http, https).start(config)
        }
    )
}

private val userRoles = setOf(
    object : UserRole {
        override val title: String = "System"
    },
    object : UserRole {
        override val title: String = "Admin"
    },
    object : UserRole {
        override val title: String = "User"
    },
)
