
import day.day
import util.Point
import util.isValid
import util.neighbors

// answer #1: 1818
// answer #2:

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
            val maxY= input.lines.count()
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
            input.lines.forEachIndexed { y, row ->
                row.forEachIndexed { x, height ->
                    print(shadow.get(Point(x, y)) ?: 0)
                    print(" ")
                }
                println()
            }
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
            val maxY= input.lines.count()
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
            val map = mutableMapOf<Point, Int>()
            input.lines.forEachIndexed { y, row ->
                row.forEachIndexed { x, height ->
                    val point = Point(x, y)
                    print(shadow.get(point) ?: 0)
                    print(" ")


                    val p = all.getValue(point)
                    val n = point.neighbors()
                        .filter { it.isValid(maxX - 1, maxY - 1) }
                        .map { all.getValue(it) }
                        .sumOf {
                            shadow.getValue(it)
                        }

                }
                println()
            }

            all.count() - shadow.count { (_, shadows) -> shadows == 4 }
        }
        part2 test 1 expect 8
    }
}

private fun Point.addNeighbors(
    maxX: Int,
    maxY: Int,
    visible: Set<Point>,
    queue: MutableList<Point>
) =
    neighbors().filter { it.isValid(maxX, maxY) && it !in visible }.also { queue.addAll(it) }