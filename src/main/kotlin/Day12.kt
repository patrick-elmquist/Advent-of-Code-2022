
import day.Input
import day.day
import util.Point
import util.neighbors
import kotlin.math.min

// answer #1: 449
// answer #2: 443

fun main() {
    day(n = 12) {
        part1(expected = 449) { input ->
            val (grid, start, end) = input.parseMapWithHeights()
            grid.findAllMinDistancesFrom(origin = end, target = start)
                .getValue(start)
        }

        part2(expected = 443) { input ->
            val (grid, _, end) = input.parseMapWithHeights()
            val startCandidates = grid.filter { (_, elevation) -> elevation == 0 }.keys
            grid.findAllMinDistancesFrom(origin = end)
                .filter { it.key in startCandidates }
                .values
                .min()
        }
    }
}

private fun Map<Point, Int>.findAllMinDistancesFrom(
    origin: Point,
    target: Point? = null
): Map<Point, Int> {
    val steps = mutableMapOf(origin to 0)
    val queue = mutableListOf(origin)
    while (queue.isNotEmpty()) {
        val point = queue.removeFirst()

        if (point == target) return steps

        val currentElevation = getValue(point)
        val currentSteps = steps.getValue(point)
        val neighborsToVisit = point.neighbors()
            .filter { it in this }
            .filter { getValue(it) >= currentElevation - 1 }
            .mapNotNull {
                val oldSteps = steps[it]
                steps[it] = min(currentSteps + 1, oldSteps ?: Int.MAX_VALUE)
                it.takeIf { steps[it] != oldSteps }
            }
        queue.addAll(neighborsToVisit)
    }
    return steps
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