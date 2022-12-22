import Dir.*
import Side.*
import day.day
import util.Point
import util.sliceByBlank

// answer #1: 109094
// answer #2: 53324

private const val WALL = '#'
private const val VOID = ' '
private const val OPEN = '.'
private fun Char.isVoid() = this == VOID

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
            solve2D(toTypedArray, instructions)
        }
        part1 test 1 expect 6032

        part2(expected = 53324) { input ->
            val sliced = input.lines.sliceByBlank()
            val matrix = sliced.first()
            val map = matrix.flatMapIndexed { y, row ->
                row.mapIndexed { x, char -> Point(x, y) to char }
            }.toMap().filterValues { it != ' ' }

            val instructions = sliced.last().first()
            solve3D(instructions, map)
        }
    }
}

private fun solve3D(allInstructions: String, map: Map<Point, Char>): Int {
    var instructions = allInstructions
    val startPos = map.entries.filter { it.key.y == 0 }.minBy { it.key.x }.key
    var position = startPos
    var direction = Right
    while (instructions.isNotEmpty()) {
        val firstTurnIdx = instructions.indexOfFirst { it == 'R' || it == 'L' }
        // MOVING
        if (firstTurnIdx > 0 || firstTurnIdx == -1) {
            val move = if (firstTurnIdx == -1) {
                instructions.toInt()
            } else {
                instructions.substring(0, firstTurnIdx).toInt()
            }
            instructions = if (firstTurnIdx == -1) {
                ""
            } else {
                instructions.substring(firstTurnIdx)
            }
            for (i in 0 until move) {
                val (nextPos, nextDir) = if (map.containsKey(position + direction.p)) {
                    Pair(position + direction.p, direction)
                } else {
                    val currentSide = sideOf(position)
                    wrapCube(position, direction, currentSide)
                }
                if (map[nextPos] == '#') {
                    break
                } else {
                    position = nextPos
                    direction = nextDir
                }
            }
        } else {
            direction = if (instructions.first() == 'R') {
                direction.clockwise()
            } else {
                direction.counterClockwise()
            }
            instructions = instructions.drop(1)
        }
    }
    return 1000 * (position.y + 1) + 4 * (position.x + 1) + direction.ordinal
}

private fun wrapCube(position: Point, direction: Dir, side: Side) =
    when {
        side == A && direction == Up ->
            Point(0, 3 * 50 + position.x - 50) to Right

        side == A && direction == Left ->
            Point(0, 2 * 50 + (50 - position.y - 1)) to Right

        side == B && direction == Up ->
            Point(position.x - 100, 199) to Up

        side == B && direction == Right ->
            Point(99, (50 - position.y) + 2 * 50 - 1) to Left

        side == B && direction == Down ->
            Point(99, 50 + (position.x - 2 * 50)) to Left

        side == C && direction == Right ->
            Point((position.y - 50) + 2 * 50, 49) to Up

        side == C && direction == Left ->
            Point(position.y - 50, 100) to Down

        side == E && direction == Left ->
            Point(50, 50 - (position.y - 2 * 50) - 1) to Right

        side == E && direction == Up ->
            Point(50, 50 + position.x) to Right

        side == D && direction == Down ->
            Point(49, 3 * 50 + (position.x - 50)) to Left

        side == D && direction == Right ->
            Point(149, 50 - (position.y - 50 * 2) - 1) to Left

        side == F && direction == Right ->
            Point((position.y - 3 * 50) + 50, 149) to Up

        side == F && direction == Left ->
            Point(50 + (position.y - 3 * 50), 0) to Down

        side == F && direction == Down ->
            Point(position.x + 100, 0) to Down

        else -> position to direction
    }

enum class Side { A, B, C, D, E, F }

private fun sideOf(p: Point) = when {
    p.x in 50..99 && p.y in 0..49 -> A
    p.x in 100..149 && p.y in 0..49 -> B
    p.x in 50..99 && p.y in 50..99 -> C
    p.x in 50..99 && p.y in 100..149 -> D
    p.x in 0..49 && p.y in 100..149 -> E
    p.x in 0..49 && p.y in 150..199 -> F
    else -> error("wtf $p")
}

enum class Dir(val p: Point) {
    Right(Point(1, 0)),
    Down(Point(0, 1)),
    Left(Point(-1, 0)),
    Up(Point(0, -1));

    fun clockwise() = values()[(this.ordinal + 1).mod(values().size)]
    fun counterClockwise() = values()[(this.ordinal - 1).mod(values().size)]
}

private fun solve2D(
    matrix: Array<Array<Char>>,
    instructions: String
): Int {
    var position = Point(matrix.first().indexOfFirst { it == '.' }, 0)
    var direction = Right
    val positions = mutableMapOf(position to direction)
    var instruction = instructions
    var count = 0
    while (instruction.isNotEmpty()) {
        instruction = if (instruction.first().isDigit()) {
            val n = instruction.takeWhile { it.isDigit() }
            val path = walk(position, direction, n.toInt(), matrix)
            positions += path.map { it to direction }
            position = path.last()
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

    return 1000 * (position.y + 1) + 4 * (position.x + 1) + direction.ordinal
}

private fun wrapRightOrDown(array: Array<Char>): Int =
    array.withIndex().first { (_, c) -> c != VOID }.index

private fun wrapLeftOrUp(array: Array<Char>): Int =
    array.withIndex().last { (_, c) -> c != VOID }.index

private fun walk(point: Point, dir: Dir, steps: Int, matrix: Array<Array<Char>>): List<Point> {
    val list = mutableListOf(point)
    var x = point.x
    var y = point.y
    val row = matrix[y]
    val col = matrix.map { it[x] }.toTypedArray()
    repeat(steps) {
        when (dir) {
            Left -> {
                x--
                if (x < 0 || matrix[y][x].isVoid()) {
                    x = wrapLeftOrUp(row)
                }
            }

            Right -> {
                x++
                if (x > row.lastIndex || matrix[y][x].isVoid()) {
                    x = wrapRightOrDown(row)
                }
            }

            Up -> {
                y--
                if (y < 0 || matrix[y][x].isVoid()) {
                    y = wrapLeftOrUp(col)
                }
            }

            Down -> {
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
