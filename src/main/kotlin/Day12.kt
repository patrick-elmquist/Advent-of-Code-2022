
import day.Input
import day.day
import util.Point
import util.neighbors
import java.util.*
import kotlin.math.min

// answer #1: 449
// answer #2: 443

fun main() {
    day(n = 12) {
        part1(expected = 449) { input ->
            val (grid, start, end) = input.parse()
            findMinStepsFromStartToEnd(grid, start, end)
        }
        part1 test 1 expect 31

        part2(expected = 443) { input ->
            val (grid, _, end) = input.parse()
            var result = Int.MAX_VALUE
            input.lines.forEachIndexed { i, row ->
                row.forEachIndexed { j, c ->
                    if (c == 'S' || c == 'a') {
                        result = min(
                            result,
                            findMinStepsFromStartToEnd(grid, Point(j, i), end)
                        )
                    }
                }
            }
            result
        }
        part2 test 1 expect 29
    }
}

private fun findMinStepsFromStartToEnd(
    grid: List<List<Int>>,
    start: Point,
    end: Point
): Int {
    val width = grid.first().indices
    val height = grid.indices
    val steps = mutableMapOf(start to 0)
    val queue = LinkedList<Point>()
    queue.add(start)
    while (queue.isNotEmpty()) {
        val point = queue.pop()

        val currentElevation = grid[point.y][point.x]
        val currentSteps = steps.getValue(point)
        point.neighbors()
            .filter { it.x in width && it.y in height }
            .filter { grid[it.y][it.x] <= currentElevation + 1 }
            .forEach {
                val oldStep = steps[it]
                steps[it] = if (oldStep == null) {
                    currentSteps + 1
                } else {
                    min(currentSteps + 1, oldStep)
                }
                if (oldStep != steps.getValue(it)) {
                    queue.add(it)
                }
            }
    }
    return steps[end] ?: Int.MAX_VALUE
}

private fun Input.parse(): MapWithHeights {
    var start: Point? = null
    var end: Point? = null
    val grid = lines.mapIndexed { i, line ->
        line.mapIndexed { j, c ->
            when (c) {
                'S' -> 0.also { start = Point(j, i) }
                'E' -> ('z' - 'a').also { end = Point(j, i) }
                else -> c - 'a'
            }
        }
    }

    return MapWithHeights(
        grid,
        requireNotNull(start),
        requireNotNull(end)
    )
}

data class MapWithHeights(val grid: List<List<Int>>, val start: Point, val end: Point)