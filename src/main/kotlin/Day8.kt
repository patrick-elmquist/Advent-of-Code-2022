
import common.log
import day.Input
import day.day
import util.Point

// answer #1: 1818
// answer #2: 368368

fun main() {
    day(n = 8) {
        part1 { input ->
            val grid = input.lines.mapIndexed { y, row ->
                row.mapIndexed { x, height -> Point(x, y) to height.digitToInt() }
            }

            val shadow = mutableMapOf<Point, Int>()
            val all = grid.flatten().toMap()

            grid.forEachIndexed { y, row ->
                var most = row.first().second
                row.drop(1).forEachIndexed { x, (point, height) ->
                    if (height <= most) {
                        shadow[point] = shadow.getOrDefault(point, 0) + 1
                    } else {
                        most = height
                    }
                }
                most = row.last().second
                row.dropLast(1).reversed().forEachIndexed { x, (point, height) ->
                    if (height <= most) {
                        shadow[point] = shadow.getOrDefault(point, 0) + 1
                    } else {
                        most = height
                    }
                }
            }
            val maxX = input.lines.first().count()
            val maxY = input.lines.count()
            var mostDown = -1
            var mostUp = -1
            for (x in 0 until maxX) {
                mostDown = grid[0][x].second
                mostUp = grid[maxY - 1][x].second
                for (y in 1 until maxY) {
                    val (point, height) = grid.get(y).get(x)
                    if (height <= mostDown) {
                        shadow[point] = shadow.getOrDefault(point, 0) + 1
                    } else {
                        mostDown = height
                    }
                    val (point2, height2) = grid.get(maxY - 1 - y).get(x)
                    if (height2 <= mostUp) {
                        shadow[point2] = shadow.getOrDefault(point2, 0) + 1
                    } else {
                        mostUp = height2
                    }
                }
            }
//            input.lines.forEachIndexed { y, row ->
//                row.forEachIndexed { x, height ->
//                    print(shadow.get(Point(x, y)) ?: 0)
//                    print(" ")
//                }
//                println()
//            }
            all.count() - shadow.count { (_, shadows) -> shadows == 4 }
        }
        part1 test 1 expect 21

        part2 { input ->

            val grid = input.lines.mapIndexed { y, row ->
                row.mapIndexed { x, height -> Point(x, y) to height.digitToInt() }
            }

            val shadow = mutableMapOf<Point, Int>()
            val all = grid.flatten().toMap()

            grid.forEachIndexed { y, row ->
                var most = row.first().second
                row.drop(1).forEachIndexed { x, (point, height) ->
                    if (height <= most) {
                        shadow[point] = shadow.getOrDefault(point, 0) + 1
                    } else {
                        most = height
                    }
                }
                most = row.last().second
                row.dropLast(1).reversed().forEachIndexed { x, (point, height) ->
                    if (height <= most) {
                        shadow[point] = shadow.getOrDefault(point, 0) + 1
                    } else {
                        most = height
                    }
                }
            }
            val maxX = input.lines.first().count()
            val maxY = input.lines.count()
            var mostDown = -1
            var mostUp = -1
            for (x in 0 until maxX) {
                mostDown = grid[0][x].second
                mostUp = grid[maxY - 1][x].second
                for (y in 1 until maxY) {
                    val (point, height) = grid.get(y).get(x)
                    if (height <= mostDown) {
                        shadow[point] = shadow.getOrDefault(point, 0) + 1
                    } else {
                        mostDown = height
                    }
                    val (point2, height2) = grid.get(maxY - 1 - y).get(x)
                    if (height2 <= mostUp) {
                        shadow[point2] = shadow.getOrDefault(point2, 0) + 1
                    } else {
                        mostUp = height2
                    }
                }
            }
            input.lines.flatMapIndexed { y, row ->
                row.mapIndexed { x, height ->
                    val height = height.digitToInt()
                    val point = Point(x, y)
                    val up = point.getAllInDirection(Direction.Up, input)
                        .map { all.getValue(it) }.score(height)
                    val down = point.getAllInDirection(Direction.Down, input)
                        .map { all.getValue(it) }.score(height)
                    val left = point.getAllInDirection(Direction.Left, input)
                        .map { all.getValue(it) }.score(height)
                    val right = point.getAllInDirection(Direction.Right, input)
                        .map { all.getValue(it) }.score(height)
                    (up * down * left * right).also { it.log("Score p:$point ") }
                }
            }.max().log("MAX:")
        }
        part2 test 1 expect 8
    }
}

private fun List<Int>.score(height: Int): Int {
    if (isEmpty()) return 0
    if (first() >= height) return 1
    var score = 0
    forEach {
        if (it < height) {
            score++
        } else {
            score++
            return score
        }
    }
    return score
}

private enum class Direction { Up, Down, Left, Right }

private fun Point.getAllInDirection(
    direction: Direction,
    input: Input
) = getAllInDirection(direction, input.lines.first().count(), input.lines.count())

private fun Point.getAllInDirection(
    direction: Direction,
    lenX: Int,
    lenY: Int
): List<Point> =
    when (direction) {
        Direction.Up -> {
            if (y == 0) emptyList()
            else (y - 1 downTo 0).map { Point(x, it) }
        }
        Direction.Down -> {
            if (y == lenY - 1) emptyList()
            else (y + 1 until lenY).map { Point(x, it) }
        }
        Direction.Left -> {
            if (x == 0) emptyList()
            else (x - 1 downTo 0).map { Point(it, y) }
        }
        Direction.Right -> {
            if (x == lenX - 1) emptyList()
            else (x + 1 until lenX).map { Point(it, y) }
        }
    }