package net.slingspot.server

import java.nio.file.Path

public data class Content(
    public val contentPath: Path,
    public val developmentPath: Path,
    public val productionPath: Path
)
