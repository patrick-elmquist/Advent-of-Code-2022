import day.day
import util.Point
import util.log
import util.sliceByBlank

// answer #1: 109094
// answer #2:

fun main() {
    day(n = 22) {
        part1(expected = 109094) { input ->
            val sliced = input.lines.sliceByBlank()
            val matrix = sliced.first()
            val maxX = matrix.maxOf { it.length }
            val instructions = sliced.last().first()

            val toTypedArray = matrix.map { row ->
                Array(maxX) { i ->
                    if (i in row.indices) row[i] else ' '
                }
            }.toTypedArray()
            val (pos, dir) = solve(toTypedArray, instructions)
            (pos.x + 1) * 4 + (pos.y + 1) * 1000 + dir.score()
        }
        part1 test 1 expect 6032

        part2 { input ->

        }
        part2 test 1 expect Unit
    }
}

private fun solve(matrix: Array<Array<Char>>, instructions: String): Pair<Point, Dir> {
    var position = Point(matrix.first().indexOfFirst { it == '.' }, 0)
    var direction = Dir.Right
    val positions = mutableMapOf(position to direction)
    var instruction = instructions
    print(matrix, positions)
    println()
    var count = 0
    while (instruction.isNotEmpty()) {
        count.log("count=")
        instruction = if (instruction.first().isDigit()) {
            val n = instruction.takeWhile { it.isDigit() }
            val path = walk(position, direction, n.toInt(), matrix)
            positions += path.map { it to direction }
            position = path.last()
//            print(matrix, positions)
            instruction.removePrefix(n)
        } else {
            direction = when (instruction.first()) {
                'R' -> direction.clockwise()
                'L' -> direction.counterClockwise()
                else -> error("")
            }
            instruction.drop(1)
        }
        count++
    }
    return position to direction
}

private fun wrapRightOrDown(array: Array<Char>): Int {
    val trimmed = array.withIndex().filter { (_, c) -> c != VOID }
    return trimmed.first().index
}

// issue is probably that i stop when reaching 0 or last but there could be a lot of void there
private fun wrapLeftOrUp(array: Array<Char>): Int {
    val trimmed = array.withIndex().filter { (_, c) -> c != VOID }
    return trimmed.last().index
}

private fun walk(point: Point, dir: Dir, steps: Int, matrix: Array<Array<Char>>): List<Point> {
    "trying to move $steps to the $dir".log()
    val list = mutableListOf(point)
    var x = point.x
    var y = point.y
    val row = matrix[y]
    val col = matrix.map { it[x] }.toTypedArray()
    repeat(steps) {
        it.log("current x=$x y=$y rowLast=${row.lastIndex} colLast=${col.lastIndex} i=")
        when (dir) {
            Dir.Left -> {
                x--
                if (x < 0 || matrix[y][x].isVoid()) {
                    x = wrapLeftOrUp(row)
                }
            }
            Dir.Right -> {
                x++
                if (x > row.lastIndex || matrix[y][x].isVoid()) {
                    x = wrapRightOrDown(row)
                }
            }
            Dir.Up -> {
                y--
                if (y < 0 || matrix[y][x].isVoid()) {
                    y = wrapLeftOrUp(col)
                }
            }
            Dir.Down -> {
                y++
                if (y > col.lastIndex || matrix[y][x].isVoid()) {
                    y = wrapRightOrDown(col)
                }
            }
        }

        when (matrix[y][x]) {
            WALL -> return list
            OPEN -> list += Point(x, y)
        }
    }
    return list
}

private const val WALL = '#'
private const val VOID = ' '
private const val OPEN = '.'
private fun Char.isVoid() = this == VOID

private fun print(matrix: Array<Array<Char>>, positions: Map<Point, Dir>) {
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
}.also { log { "changed dir clockwise from=$this to=$it" } }

private fun Dir.counterClockwise() = when (this) {
    Dir.Right -> Dir.Up
    Dir.Down -> Dir.Right
    Dir.Left -> Dir.Down
    Dir.Up -> Dir.Left
}.also { log { "changed dir counter clockwise from=$this to=$it" } }

private enum class Dir { Left, Up, Right, Down }
