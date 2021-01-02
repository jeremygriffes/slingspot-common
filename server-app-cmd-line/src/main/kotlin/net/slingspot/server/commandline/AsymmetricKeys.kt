package net.slingspot.server.commandline

import java.io.File
import java.security.Key
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import kotlin.reflect.full.allSuperclasses

/**
 * Generates a Key from a byte array.
 *
 * Supported algorithms:
 * - RSA
 *
 */
public inline fun <reified T : Key> keyFrom(bytes: ByteArray): T =
    T::class.allSuperclasses.let { type ->
        KeyFactory.getInstance(
            when {
                type.contains(RSAKey::class) -> "RSA"
                else -> throw UnsupportedKeyException("Algorithm unsupported for ${T::class}")
            }
        ).run {
            when {
                type.contains(PublicKey::class) -> generatePublic(X509EncodedKeySpec(bytes))
                type.contains(PrivateKey::class) -> generatePrivate(PKCS8EncodedKeySpec(bytes))
                else -> throw UnsupportedKeyException("No supported type for ${T::class}")
            }
        }
    } as T

/**
 * Convenience method to read a key from a file at [filePath].
 *
 * @see keyFrom
 */
public inline fun <reified T : Key> keyFrom(filePath: String): T = keyFrom(File(filePath).readBytes())

public class UnsupportedKeyException(message: String) : RuntimeException(message)
