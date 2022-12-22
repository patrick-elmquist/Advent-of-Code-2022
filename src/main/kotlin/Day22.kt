import day.day
import util.Point
import util.sliceByBlank

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
    val positions = mutableListOf(point)

    

    return positions
}

private fun print(matrix: Array<CharArray>, positions: Map<Point, Dir>) {
    matrix.forEachIndexed { i, row ->
        row.forEachIndexed { j, c ->
            val p = Point(j, i)
            val dir = positions[p]
            val char = if (dir != null) {
                when (dir) {
                    Dir.Right -> '>'
                    Dir.Down -> 'v'
                    Dir.Left -> '<'
                    Dir.Up -> '^'
                }
            } else c
            print(char)
        }
        println()
    }
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
}

private fun Dir.counterClockwise() = when (this) {
    Dir.Right -> Dir.Up
    Dir.Down -> Dir.Right
    Dir.Left -> Dir.Down
    Dir.Up -> Dir.Left
}

private enum class Dir { Left, Up, Right, Down }

//private sealed class MapTile {
//    object Wall : MapTile()
//    object Open : MapTile()
//
//    // map is <directionFrom, to>
//    data class Portal(val destination: Map<Int, Point>)
//}
//

//fun start() {
//
//    val map = mutableMapOf<Point, MapTile>()
//    val matrix = sliced.first()
//    fun findSurroundings(point: Point): Map<Point, MapTile> {
//        val n = point.neighbors()
//
//        val left = point - Point(1, 0)
//        if (matrix[left.y][left.x] == ' ') {
//            var p = left
//            while (matrix[p.y][p.x] != ' ') {
//                if (p.)
//            }
//        }
//
//        val right = point + Point(1, 0)
//        val top = point - Point(0, 1)
//        val bottom = point + Point(0, 1)
//
//        TODO()
//    }
//    sliced.first().forEachIndexed { i, row ->
//        row.forEachIndexed { j, c ->
//            val point = Point(i, j)
//            when (c) {
//                '.' -> {
//                    map[point] = MapTile.Open
//                    map += findSurroundings(point)
//                }
//                '#' -> {
//                    map[point] = MapTile.Wall
//                    map += findSurroundings(point)
//                }
//            }
//        }
//    }
//}