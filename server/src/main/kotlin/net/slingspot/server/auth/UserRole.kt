package net.slingspot.server.auth

public interface UserRole {
    public val title: String

    public companion object {
        public val PUBLIC: List<UserRole> = listOf()
    }
}
