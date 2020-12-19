package net.slingspot.net

private const val decimalByte = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"
private const val hextet = "(?:[A-Fa-f0-9]{1,4})"

private val ipV4 = "^(?:$decimalByte\\.){3}$decimalByte$".toRegex()
private val ipV6 = "^(?:$hextet:){7}$hextet$".toRegex()

public fun validV4AddressFrom(address: String): String? = address.takeIf { it.matches(ipV4) }

public fun validV6AddressFrom(address: String): String? = address.takeIf { it.matches(ipV6) }

public fun validPortFrom(port: Int?): Int? = port.takeIf { it in 1..65535 }

public fun validPortFrom(port: String?): Int? = validPortFrom(port?.toIntOrNull())

public enum class AddressVersion {
    IpV4,
    IpV6,
    Unrecognized,
    ;

    public companion object {
        public fun from(address: String): AddressVersion = when {
            address.matches(ipV4) -> IpV4
            address.matches(ipV6) -> IpV6
            else -> Unrecognized
        }
    }
}
