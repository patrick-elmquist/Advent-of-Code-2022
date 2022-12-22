import Dir2.*
import Side.*
import day.day
import util.Point
import util.log
import util.sliceByBlank

// answer #1: 109094
// answer #2:

private const val WALL = '#'
private const val VOID = ' '
private const val OPEN = '.'
private fun Char.isVoid() = this == VOID

fun main() {
    day(n = 22) {
        ignorePart1 = true
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
            val (pos, dir) = solve(toTypedArray, instructions, ::walk)
            (pos.x + 1) * 4 + (pos.y + 1) * 1000 + dir.score()
        }
        part1 test 1 expect 6032

        part2 { input ->
            val sliced = input.lines.sliceByBlank()
            val matrix = sliced.first()
            val map = matrix.flatMapIndexed { y, row ->
                row.mapIndexed { x, char -> Point(x, y) to char }
            }.toMap().filterValues { it != ' ' }

            val instructions = sliced.last().first()
            solve3D(instructions, map)

        }
//        part2 test 1 expect 5031
    }
}

private fun solve3D(
    instructions: String,
    map: Map<Point, Char>,
): Int {
    var instr = instructions
    val startPos = map.entries.filter { it.key.y == 0 }.minBy { it.key.x }.key
    var currPos = startPos
    var currDir = RIGHT
    while (instr.isNotEmpty()) {
        val firstTurnIdx = instr.indexOfFirst { it == 'R' || it == 'L' }
        // MOVING
        if (firstTurnIdx > 0 || firstTurnIdx == -1) {
            val move = if (firstTurnIdx == -1) instr.toInt() else instr.substring(0, firstTurnIdx).toInt()
            instr = if (firstTurnIdx == -1) "" else instr.substring(firstTurnIdx)
            for (i in 0 until move) {
                val (nextPos, nextDir) =
                    if (map.containsKey(currPos + currDir.p)) { Pair(currPos + currDir.p, currDir) } // Normal move
                    else { cubeWrap(currPos, currDir) } //Wrap around
                if (map[nextPos] == '#') break
                else {
                    currPos = nextPos
                    currDir = nextDir
                }
            }
        } else { // TURNING
            currDir = if (instr[0] == 'R') { currDir.right() } else { currDir.left() }
            instr = instr.drop(1)
        }
    }
    return 1000 * (currPos.y + 1) + 4 * (currPos.x + 1) + currDir.ordinal
}

fun cubeWrap(curr: Point, currDir: Dir2): Pair<Point, Dir2> {
    var nextDir = currDir
    val currSide = sideOf(curr)
    var nextPos = curr
    if (currSide == A && currDir == UP) {
        nextDir = RIGHT
        nextPos = Point(0, 3 * 50 + curr.x - 50) // nextSide = F
    } else if (currSide == A && currDir == LEFT) {
        nextDir = RIGHT
        nextPos = Point(0, 2 * 50 + (50 - curr.y - 1)) // nextSide = E
    } else if (currSide == B && currDir == UP) {
        nextDir = UP
        nextPos = Point(curr.x - 100, 199) // nextSide = F
    } else if (currSide == B && currDir == RIGHT) {
        nextDir = LEFT
        nextPos = Point(99, (50 - curr.y) + 2 * 50 - 1) // nextSide = D
    } else if (currSide == B && currDir == DOWN) {
        nextDir = LEFT
        nextPos = Point(99, 50 + (curr.x - 2 * 50)) // nextSide = C
    } else if (currSide == C && currDir == RIGHT) {
        nextDir = UP
        nextPos = Point((curr.y - 50) + 2 * 50, 49) // nextSide = B
    } else if (currSide == C && currDir == LEFT) {
        nextDir = DOWN
        nextPos = Point(curr.y - 50, 100) // nextSide = E
    } else if (currSide == E && currDir == LEFT) {
        nextDir = RIGHT
        nextPos = Point(50, 50 - (curr.y - 2 * 50) - 1) // nextSide = A
    } else if (currSide == E && currDir == UP) {
        nextDir = RIGHT
        nextPos = Point(50, 50 + curr.x) // nextSide = C
    } else if (currSide == D && currDir == DOWN) {
        nextDir = LEFT
        nextPos = Point(49, 3 * 50 + (curr.x - 50)) // nextSide = F
    } else if (currSide == D && currDir == RIGHT) {
        nextDir = LEFT
        nextPos = Point(149, 50 - (curr.y - 50 * 2) - 1) // nextSide = B
    } else if (currSide == F && currDir == RIGHT) {
        nextDir = UP
        nextPos = Point((curr.y - 3 * 50) + 50, 149) // nextSide = D
    } else if (currSide == F && currDir == LEFT) {
        nextDir = DOWN
        nextPos = Point(50 + (curr.y - 3 * 50), 0) // nextSide = A
    } else if (currSide == F && currDir == DOWN) {
        nextDir = DOWN
        nextPos = Point(curr.x + 100, 0) // nextSide = B
    }
    return Pair(nextPos, nextDir)
}

enum class Side { A, B, C, D, E, F }

private fun sideOf(pos: Point): Side {
    if (pos.x in 50..99 && pos.y in 0..49) return A
    if (pos.x in 100..149 && pos.y in 0..49) return B
    if (pos.x in 50..99 && pos.y in 50..99) return C
    if (pos.x in 50..99 && pos.y in 100..149) return D
    if (pos.x in 0..49 && pos.y in 100..149) return E
    if (pos.x in 0..49 && pos.y in 150..199) return F
    throw java.lang.RuntimeException("Side does not exist for $pos")
}
enum class Dir2(val p: Point) { RIGHT(Point(1, 0)), DOWN(Point(0, 1)), LEFT(Point(-1, 0)), UP(Point(0, -1));
    fun right() = values()[(this.ordinal + 1).mod(values().size)]
    fun left() = values()[(this.ordinal - 1).mod(values().size)]
}
private fun solve(
    matrix: Array<Array<Char>>,
    instructions: String,
    walk: (Point, Dir, Int, Array<Array<Char>>) -> List<Point>
): Pair<Point, Dir> {
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

// region Part 1
private fun wrapRightOrDown(array: Array<Char>): Int =
    array.withIndex().first { (_, c) -> c != VOID }.index

private fun wrapLeftOrUp(array: Array<Char>): Int =
    array.withIndex().last { (_, c) -> c != VOID }.index

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

// endregion
