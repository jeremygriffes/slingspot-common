package net.slingspot.website.sample

import net.slingspot.server.Request
import net.slingspot.server.RoleProvider
import net.slingspot.server.UserRole

class Roles : RoleProvider {
    override val allRoles = setOf(
        AppRole.System,
        AppRole.Admin,
        AppRole.User,
    )

    override fun isAuthorized(request: Request, endpointRoles: Set<UserRole>): Boolean {
        TODO("Not yet implemented")
    }

    enum class AppRole(override val title: String) : UserRole {
        System("system"),
        Admin("admin"),
        User("user"),
    }
}
