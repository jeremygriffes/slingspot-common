package net.slingspot.log

import net.slingspot.io.DirectoryRef
import net.slingspot.io.FileRef
import net.slingspot.io.FileSystem
import net.slingspot.io.TextFileRef

class MockFileSystem : FileSystem {
    val affectedPaths = mutableMapOf<String, MockFileRef>()

    override val separator = "/"

    override fun fileAt(path: String): FileRef {
        return affectedPaths[path] ?: MockFileRef(path).also {
            affectedPaths[path] = it
        }
    }

    override fun textFileAt(path: String): TextFileRef {
        return affectedPaths[path] as? TextFileRef ?: MockTextFileRef(path).also {
            affectedPaths[path] = it
        }
    }

    override fun directoryAt(path: String): DirectoryRef {
        return affectedPaths[path] as? MockDirectoryRef ?: MockDirectoryRef(path).also {
            affectedPaths[path] = it
        }
    }

    private fun insertIntoParentDirectory(file: MockFileRef) {
        (affectedPaths[file.path.substringBeforeLast(separator)] as? MockDirectoryRef)?.contents?.add(file)
    }

    private fun removeFromParentDirectory(file: MockFileRef) {
        (affectedPaths[file.path.substringBeforeLast(separator)] as? MockDirectoryRef)?.contents?.remove(file)
    }

    open inner class MockFileRef(final override val path: String) : FileRef {
        var created = false
        var deleted = false
        var exists = false
        var isDirectory = false

        override fun create() {
            created = true
            exists = true
            insertIntoParentDirectory(this)
        }

        override fun delete() {
            deleted = true
            exists = false
            removeFromParentDirectory(this)
        }

        override fun exists(): Boolean {
            return exists
        }

        override fun length(): Long {
            return 0L
        }

        override fun asDirectory(): DirectoryRef? {
            return if (isDirectory) MockDirectoryRef(path) else null
        }

        override fun compareTo(other: FileRef): Int {
            return path.compareTo((other as MockFileRef).path)
        }
    }

    internal open inner class MockTextFileRef(path: String) : MockFileRef(path), TextFileRef {
        val ledger = StringBuilder()

        override fun append(text: String) {
            ledger.append(text)
            insertIntoParentDirectory(this)
        }

        override fun length(): Long {
            return ledger.length.toLong()
        }
    }

    internal inner class MockDirectoryRef(path: String) : MockFileRef(path), DirectoryRef {
        init {
            isDirectory = true
        }

        val contents = mutableSetOf<MockFileRef>()

        override fun contents(): List<FileRef> {
            return contents.sorted()
        }
    }
}
