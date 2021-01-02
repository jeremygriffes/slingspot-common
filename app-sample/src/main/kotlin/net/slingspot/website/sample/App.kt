package net.slingspot.website.sample

import net.slingspot.log.Log.loggers
import net.slingspot.server.Config
import net.slingspot.server.auth.Authorization
import net.slingspot.server.auth.UserRole
import net.slingspot.server.commandline.parse
import java.security.PrivateKey
import java.security.PublicKey

private const val publicResourceDirectory = "public"

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

fun main(vararg args: String) {
    parse(*args) {
        with(it) {
            loggers = listOfNotNull(consoleLogger, fileLogger)

            AppServer(httpPort, httpsPort).start(
                Config(environment, certKeystore, publicResourceDirectory, authorization(publicKey, privateKey))
            )
        }
    }
}

private fun authorization(public: PublicKey, private: PrivateKey?) = object : Authorization {
    override val allRoles: Set<UserRole> = userRoles
    override val publicKey: PublicKey = public
    override val privateKey: PrivateKey? = private
}