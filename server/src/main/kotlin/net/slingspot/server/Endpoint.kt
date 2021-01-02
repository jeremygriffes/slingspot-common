package net.slingspot.server

import net.slingspot.server.auth.UserRole

public interface Endpoint {
    public val method: Method
    public val path: String
    public val access: List<UserRole>

    /**
     * This is not an exhaustive list of methods. Add new ones as needed.
     */
    public enum class Method {
        Get,
        Put,
        Post,
        Delete,
        ;

        public companion object {
            public fun from(string: String): Method = when (string) {
                "GET" -> Get
                "PUT" -> Put
                "POST" -> Post
                "DELETE" -> Delete
                else -> throw RuntimeException("Unhandled method type")
            }
        }
    }

    public fun process(request: Request, response: Response)

    public interface Error {
        public val statusCode: Int

        public fun process(request: Request, response: Response)
    }
}
