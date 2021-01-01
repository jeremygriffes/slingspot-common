package net.slingspot.io

import java.io.File
import java.io.IOException

public class DefaultFileSystem : FileSystem {
    override val separator: String = File.separator

    override fun fileAt(path: String): FileRef {
        return DefaultFileRef(path)
    }

    override fun textFileAt(path: String): TextFileRef {
        return DefaultTextFileRef(path)
    }

    override fun directoryAt(path: String): DirectoryRef {
        return DefaultDirectoryRef(path)
    }

    internal open class DefaultFileRef(final override val path: String) : FileRef {
        protected val file: File = File(path)

        override fun create() {
            file.createNewFile()
        }

        override fun delete() {
            file.delete()
        }

        override fun exists(): Boolean {
            return file.exists()
        }

        override fun length(): Long {
            return file.length()
        }

        override fun asDirectory(): DirectoryRef? {
            return if (file.isDirectory) DefaultDirectoryRef(path) else null
        }

        override fun compareTo(other: FileRef): Int {
            return file.compareTo((other as DefaultFileRef).file)
        }

        override fun equals(other: Any?) = this === other || (other is DefaultFileRef && file == other.file)

        override fun hashCode() = file.hashCode()
    }

    internal open class DefaultTextFileRef(path: String) : DefaultFileRef(path), TextFileRef {
        override fun append(text: String) {
            file.appendText(text)
        }
    }

    internal class DefaultDirectoryRef(path: String) : DefaultFileRef(path), DirectoryRef {
        override fun create() {
            file.mkdirs()
        }

        override fun contents(): List<FileRef> {
            return file.listFiles()?.sorted()?.map { DefaultFileRef(it.absolutePath) }
                ?: throw IOException("Directory does not exist")
        }
    }
}
