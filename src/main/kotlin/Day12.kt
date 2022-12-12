
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
            val (grid, start, end) = input.parseMapWithHeights()
            findMinStepsFromStartToEnd(grid, start, end)
        }

        part2(expected = 443) { input ->
            val (grid, _, end) = input.parseMapWithHeights()
            grid.filter { (_, elevation) -> elevation == 0 }
                .keys
                .minOf { start -> findMinStepsFromStartToEnd(grid, start, end) }
        }
    }
}

private fun findMinStepsFromStartToEnd(grid: Map<Point, Int>, start: Point, end: Point): Int {
    val steps = mutableMapOf(start to 0)
    val queue = LinkedList<Point>().apply { add(start) }
    while (queue.isNotEmpty()) {
        val point = queue.pop()
        val currentElevation = grid.getValue(point)
        val currentSteps = steps.getValue(point)
        val neighborsToVisit = point.neighbors()
            .filter { it in grid }
            .filter { grid.getValue(it) <= currentElevation + 1 }
            .mapNotNull {
                val oldSteps = steps[it]
                val newSteps = min(currentSteps + 1, oldSteps ?: Int.MAX_VALUE)
                steps[it] = min(currentSteps + 1, oldSteps ?: Int.MAX_VALUE)
                it.takeIf { newSteps != oldSteps }
            }
        queue.addAll(neighborsToVisit)
    }
    return steps[end] ?: Int.MAX_VALUE
}

private fun Input.parseMapWithHeights(): MapWithHeights {
    var start: Point? = null
    var end: Point? = null
    val grid = lines.flatMapIndexed { i, line ->
        line.mapIndexed { j, c ->
            val point = Point(j, i)
            val elevation = when (c) {
                'S' -> 0.also { start = point }
                'E' -> ('z' - 'a').also { end = point }
                else -> c - 'a'
            }
            point to elevation
        }
    }.toMap()

    return MapWithHeights(
        grid,
        requireNotNull(start),
        requireNotNull(end)
    )
}

private data class MapWithHeights(val grid: Map<Point, Int>, val start: Point, val end: Point)
