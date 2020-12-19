package net.slingspot.server

public interface Response {
    public var statusCode: Int?

    public fun html(html: String)
    public fun json(json: Any)
    public fun render(filePath: String, model: Map<String, Any?> = mapOf())
}
