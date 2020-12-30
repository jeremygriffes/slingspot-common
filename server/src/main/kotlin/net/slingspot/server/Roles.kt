package net.slingspot.server

public interface RoleProvider {
    public val allRoles: Set<UserRole>

    public fun isAuthorized(request: Request, endpointRoles: Set<UserRole>): Boolean
}

public interface UserRole {
    public val title: String

    public companion object {
        public val EVERYONE: List<UserRole> = listOf()
    }
}
