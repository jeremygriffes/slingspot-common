package net.slingspot.server.commandline

import net.slingspot.log.ConsoleLogger
import net.slingspot.log.FileLogger
import net.slingspot.server.CertKeystore
import net.slingspot.server.Environment
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

/**
 * Represents all externally configurable components of a server
 */
public data class Arguments(
    val httpPort: Int,
    val httpsPort: Int,
    val environment: Environment,
    val certKeystore: CertKeystore,
    val publicKey: RSAPublicKey,
    val privateKey: RSAPrivateKey?,
    val consoleLogger: ConsoleLogger?,
    val fileLogger: FileLogger?,
)
