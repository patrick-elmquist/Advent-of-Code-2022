
import day.day
import util.sliceBy

// answer #1: 1297159
// answer #2: 3866390

private const val AT_MOST = 100000L
private const val TOTAL = 70000000L
private const val TARGET = 30000000L

fun main() {
    day(n = 7) {
        part1(expected = 1297159L) { input ->
            val root = createFileTree(input.lines)
            root.listAllNestedFolders()
                .filter { it.size <= AT_MOST }
                .sumOf { it.size }
        }

        part2(expected = 3866390L) { input ->
            val root = createFileTree(input.lines)
            val spaceNeeded = TARGET - (TOTAL - root.size)
            root.listAllNestedFolders()
                .map { it.size }
                .sorted()
                .first { it > spaceNeeded }
        }
    }
}

private fun List<String>.groupByCommandAndOutput() =
    sliceBy { _, line -> line.first() == '$' }

private fun createFileTree(lines: List<String>): Content.Dir {
    val root = Content.Dir(name = "/", parent = null)
    lines.groupByCommandAndOutput()
        .drop(1)
        .fold(root) { currentDir, block ->
            Command.from(block).executeIn(currentDir)
        }
    return root
}

private sealed class Command {
    abstract fun executeIn(current: Content.Dir): Content.Dir
    private data class Cd(val next: String) : Command() {
        override fun executeIn(current: Content.Dir): Content.Dir =
            when (next) {
                ".." -> requireNotNull(current.parent)
                else -> {
                    current.content
                        .filterIsInstance(Content.Dir::class.java)
                        .first { it.name == next }
                }
            }
    }

    private data class Ls(val dirContent: List<String>) : Command() {
        override fun executeIn(current: Content.Dir): Content.Dir =
            current.apply { content.addAll(dirContent.map { line -> Content(line, current) }) }
    }

    companion object {
        fun from(block: List<String>): Command {
            val split = block.first().split(" ").drop(1)
            return when (val command = split.first()) {
                "cd" -> Cd(split.last())
                "ls" -> Ls(block.drop(1))
                else -> error("not a valid command $command")
            }
        }
    }
}

private sealed class Content {
    abstract val name: String
    abstract val size: Long

    class File(override val name: String, override val size: Long) : Content()

    class Dir(
        override val name: String,
        val parent: Dir?,
        val content: MutableList<Content> = mutableListOf()
    ) : Content() {
        override val size: Long by lazy { content.sumOf { it.size } }

        fun listAllNestedFolders(): List<Dir> =
            listOf(this) + content
                .filterIsInstance(Dir::class.java)
                .flatMap { it.listAllNestedFolders() }
    }

    companion object {
        operator fun invoke(line: String, current: Dir): Content {
            val (first, name) = line.split(" ")
            return when (first) {
                "dir" -> Dir(name, current)
                else -> File(name, first.toLong())
            }
        }
    }
}
