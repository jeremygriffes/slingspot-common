package net.slingspot.server

public enum class Environment {
    Development,
    Staging,
    Production,
    ;

    override fun toString(): String = when (this) {
        Development -> DEVELOPMENT
        Staging -> STAGING
        Production -> PRODUCTION
    }

    public companion object {
        private const val DEVELOPMENT = "development"
        private const val STAGING = "staging"
        private const val PRODUCTION = "production"

        public fun from(string: String?): Environment = when (string) {
            STAGING -> Staging
            PRODUCTION -> Production
            else -> Development
        }
    }
}
