import common.log
import day.Input
import day.day

// answer #1: 1297159
// answer #2: 3866390

fun <T : List<E>, E : CharSequence> T.groupByCommand() =
    (indices.filter { get(it).first() == '$' }.drop(1) + listOf(size))
        .fold(mutableListOf<List<E>>() to 0) { (list, start), end ->
            list.add(subList(start, end))
            list to end
        }.first.toList()
private const val MOST = 100000L
fun main() {
    day(n = 7) {
        part1 test 1 expect 95437L
        part1(expected = 1297159L) { input ->
            val root = Dir(name = "/", parent = null)
            traverse(root, input)
            root.findFoldersWithAtMostSize(MOST).sumOf { it.size() }
        }

        part2 { input ->
            val root = Dir(name = "/", parent = null)
            traverse(root, input)
            val totalSize = root.size()
            val lacking = TARGET - (TOTAL - totalSize)
            lacking.log("lacking")
            root.findFolders().map { it.name }.log("Folders")
            root.findFolders().mapNotNull { it as? Dir }
                .map { it.size() }
                .log()
                .sorted()
                .first { it > lacking }

        }
        part2 test 1 expect 24933642L
    }
}

private fun traverse(root: Dir, input: Input) {
    var currentDir: Dir = root
    input.lines.groupByCommand().drop(1).forEach { block ->
        val split = block.first().split(" ")
        when (split[1]) {
            "cd" -> {
                split.log("Split")
                currentDir = when (val arg = split[2]) {
                    ".." -> currentDir.parent!!.log("Changing current dir from $currentDir to ")
                    else -> {
                        currentDir.content.mapNotNull { it as? Dir }
                            .first { it.name.log("Name") == arg }
                    }
                }
            }

            "ls" -> {
                currentDir.content.addAll(
                    block.drop(1)
                        .map { line ->
                            val (first, name) = line.split(" ")
                            when (first) {
                                "dir" -> {
                                    Dir(name, currentDir)
                                }

                                else -> {
                                    File(name, first.toLong())
                                }
                            }
                        }.log("Adding to ${currentDir.name}")
                )
            }

            else -> error("")
        }
    }
}

private const val TOTAL = 70000000L
private const val TARGET = 30000000L

private sealed class Content {
    abstract val name: String
    abstract fun size(): Long
}
private data class File(override val name: String, val size: Long) : Content() {
    override fun size(): Long {
        return size
    }
}
private class Dir(
    override val name: String,
    val parent: Dir?,
    val content: MutableList<Content> = mutableListOf()
) : Content() {
    var _size = -1L
    override fun size(): Long {
        if (_size == -1L) {
            _size = content.sumOf { it.size() }
        }
        return _size
    }
    fun findFoldersWithAtMostSize(n: Long): List<Dir> {
        this.log("Inside dir:")
        if (content.none { it is Dir }) {
            return if (content.sumOf { it.size() } <= n) {
                listOf(this)
            } else {
                emptyList()
            }
        }

        val passingLists = content.filterIsInstance(Dir::class.java)
            .flatMap { it.findFoldersWithAtMostSize(n) }

        val doIPass = content.sumOf { it.size() } <= n
        return if (doIPass) {
            passingLists + listOf(this)
        } else {
            passingLists
        }
    }
    fun findFolders(): List<Dir> {
        if (content.none { it is Dir }) {
            return listOf(this)
        }
        return listOf(this) + content.mapNotNull { it as? Dir }.flatMap {
            it.findFolders()
        }
    }

    override fun toString(): String {
        return "Dir(name=$name, parent:${parent?.name}, content=$content)"
    }
}
