import day.day
import util.Point
import util.log
import util.sliceByBlank
import kotlin.math.log

// answer #1:
// answer #2:

fun main() {
    day(n = 22) {
        part1 { input ->
            val sliced = input.lines.sliceByBlank()
            val matrix = sliced.first()
            val instructions = sliced.last().first()

            val toTypedArray: Array<CharArray> = matrix.map { it.toCharArray() }.toTypedArray()
            val (pos, dir) = solve(toTypedArray, instructions)
            (pos.x + 1) * 4 + (pos.y * 1000) + dir.score()
        }
        part1 test 1 expect 6032

        part2 { input ->

        }
        part2 test 1 expect Unit
    }
}

private fun solve(matrix: Array<CharArray>, instructions: String): Pair<Point, Dir> {
    var position = Point(matrix.first().indexOfFirst { it == '.' }, 0)
    var direction = Dir.Right
    val positions = mutableMapOf(position to direction)
    var i = instructions
    print(matrix, positions)
    while (i.isNotEmpty()) {
        i = if (i.first().isDigit()) {
            val n = i.takeWhile { it.isDigit() }
            val path = walk(position, direction, n.toInt(), matrix)
            positions += path.map { it to direction }
            position = path.last()
            i.removePrefix(n)
        } else {
            val d = i.first()
            direction = when (d) {
                'R' -> direction.clockwise()
                'L' -> direction.counterClockwise()
                else -> error("")
            }
            i.drop(1)
        }
        print(matrix, positions)
    }
    return position to direction
}

private fun walk(point: Point, dir: Dir, steps: Int, matrix: Array<CharArray>): List<Point> {
    return when (dir) {
        Dir.Left -> walkLeft(point, matrix[point.y], steps)
        Dir.Right -> walkRight(point, matrix[point.y], steps)
        else -> TODO()
//        Dir.Up -> (point.y downTo point.y - steps)
//        Dir.Down -> point.y .. point.y + steps
    }
}

private fun walkLeft(point: Point, row: CharArray, steps: Int): List<Point> {
    val list = mutableListOf(point)
    var x = point.x
    val y = point.y
    repeat(steps) {
        x--
        when (row[x]) {
            '.' -> list += Point(x, y)
            '#' -> return list
            else -> {
                val otherSideX = row.drop(x).indexOfFirst { it == ' ' }
                x = when {
                    otherSideX == -1 -> row.lastIndex.takeIf { row.last() != '#' } ?: return list
                    row[otherSideX - 1] == '#' -> return list
                    else -> otherSideX
                }
                list += Point(x, y)
            }
        }
    }
    return list
}

private fun walkRight(point: Point, row: CharArray, steps: Int): List<Point> {
    val list = mutableListOf(point)
    var x = point.x
    val y = point.y
    repeat(steps) {
        x++
        when (row[x]) {
            '.' -> list += Point(x, y)
            '#' -> return list
            else -> {
                val reversed = row.take(x + 1).reversed()
                val otherSideX = reversed.indexOfFirst { it == ' ' }
                x -= when {
                    otherSideX == -1 -> reversed.lastIndex.takeIf { row.last() != '#' } ?: return list
                    row[otherSideX - 1] == '#' -> return list
                    else -> otherSideX
                }
                list += Point(x, y)
            }
        }
    }
    return list
}

private fun print(matrix: Array<CharArray>, positions: Map<Point, Dir>) {
    matrix.forEachIndexed { i, row ->
        row.forEachIndexed { j, c ->
            val char = when (positions[Point(j, i)]) {
                null -> c
                Dir.Right -> '>'
                Dir.Down -> 'v'
                Dir.Left -> '<'
                Dir.Up -> '^'
            }
            print(char)
        }
        println()
    }
    println()
}

private fun Dir.score() = when (this) {
    Dir.Right -> 0
    Dir.Down -> 1
    Dir.Left -> 2
    Dir.Up -> 3
}

private fun Dir.clockwise() = when (this) {
    Dir.Right -> Dir.Down
    Dir.Down -> Dir.Left
    Dir.Left -> Dir.Up
    Dir.Up -> Dir.Right
}.also { log { "changed dir clockwise from=$this to=$it"} }

private fun Dir.counterClockwise() = when (this) {
    Dir.Right -> Dir.Up
    Dir.Down -> Dir.Right
    Dir.Left -> Dir.Down
    Dir.Up -> Dir.Left
}.also { log { "changed dir counter clockwise from=$this to=$it"} }

private enum class Dir { Left, Up, Right, Down }
