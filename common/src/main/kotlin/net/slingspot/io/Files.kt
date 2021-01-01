package net.slingspot.io

public interface FileSystem {
    public val separator: String

    public fun fileAt(path: String): FileRef

    public fun textFileAt(path: String): TextFileRef

    public fun directoryAt(path: String): DirectoryRef
}

public interface FileRef : Comparable<FileRef> {
    public val path: String
    public fun create()
    public fun delete()
    public fun exists(): Boolean
    public fun length(): Long
    public fun asDirectory(): DirectoryRef?
}

public interface TextFileRef : FileRef {
    public fun append(text: String)
}

public interface DirectoryRef : FileRef {
    public fun contents(): List<FileRef>
}
