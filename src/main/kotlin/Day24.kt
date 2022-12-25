import day.Input
import day.day
import day.pointCharMap
import util.Point
import util.log
import util.neighbors
import java.util.*
import kotlin.math.abs
import kotlin.math.min

// answer #1: 334
// answer #2: 934

private fun Input.getStartEnd(): Pair<Point, Point> {
    val start = lines.first().indexOfFirst { it == '.' }
        .let { Point(it, 0) }
    val end = lines.last().indexOfFirst { it == '.' }
        .let { Point(it, lines.lastIndex) }
    return start to end
}

fun main() {
    day(n = 24) {
        part1(expected = 334) { input ->
            val map = input.pointCharMap.toMutableMap()

            val (start, end) = input.getStartEnd()

            val wallsAndBlizzards = map.mapValues { listOf(it.value) }
                .filter { it != empty }

            val states = generateAllMapStates(
                map = wallsAndBlizzards,
                width = input.lines.first().length,
                height = input.lines.size
            )

            solve(
                states = states,
                start = start,
                end = end,
                map = wallsAndBlizzards
            )
        }
        part1 test 2 expect 18

        part2(expected = 934) { input ->
            val map = input.pointCharMap.toMutableMap()

            val (start, end) = input.getStartEnd()

            val wallsAndBlizzards = map.mapValues { listOf(it.value) }
                .filter { it != empty }

            val states = generateAllMapStates(
                map = wallsAndBlizzards,
                width = input.lines.first().length,
                height = input.lines.size
            )

            val minutesThere = solve(
                states = states,
                start = start,
                end = end,
                map = wallsAndBlizzards
            )

            val minutesBack= solve(
                minutes = minutesThere,
                states = states,
                start = end,
                end = start,
                map = wallsAndBlizzards
            )

            solve(
                minutes = minutesBack,
                states = states,
                start = start,
                end = end,
                map = wallsAndBlizzards
            )
        }
        part2 test 2 expect (18 + 23 + 13)
    }
}

private fun generateAllMapStates(
    map: Map<Point, List<Char>>,
    width: Int,
    height: Int
): List<Map<Point, Char>> {
    var start = map
    var blizzards = start.filterValues { it != wall && it != empty }
    var flattened = blizzards.mapValues { '@' }
    val list = mutableListOf<Map<Point, Char>>()
    val walls = map.filterValues { it == wall }
    list += flattened
    var i = 0
    while (true) {
        blizzards = start.filterValues { it != wall && it != empty }
        val moved = blizzards
            .flatMap { (point, list) -> list.map { move(point, it, width, height) to it } }
            .groupBy({ it.first }, { it.second })
        flattened = moved.mapValues { '@' }
        i++
        if (flattened in list) {
            "generate finished after $i rounds".log()
            return list
        } else {
            list += flattened
            start = walls + moved
        }
    }
}
// add wrapping
private fun move(
    position: Point,
    direction: Char,
    width: Int,
    height: Int
): Point {
    return when (direction) {
        '>' -> {
            val x = (position.x + 1).takeIf { it < width - 1 }
            position.copy(x = x ?: 1)
        }

        '<' -> {
            val x = (position.x - 1).takeIf { it > 0 }
            position.copy(x = x ?: (width - 2))
        }

        'v' -> {
            val y = (position.y + 1).takeIf { it < height - 1 }
            position.copy(y = y ?: 1)
        }

        '^' -> {
            val y = (position.y - 1).takeIf { it > 0 }
            position.copy(y = y ?: (height - 2))
        }

        else -> {
            error("can't handle $direction from point $position")
        }
    }
}

private val wall = listOf('#')
private val empty = listOf('.')

private fun solve(
    states: List<Map<Point, Char>>,
    start: Point,
    end: Point,
    map: Map<Point, List<Char>>,
    minutes: Int = 0
): Int {
    val width = map.maxOf { it.key.x } - map.minOf { it.key.x } + 1
    val height = map.maxOf { it.key.y } - map.minOf { it.key.y } + 1
    val queue = PriorityQueue<MapState>()
    val initial = MapState(
        current = start,
        end = end,
        minutes = minutes
    )
    queue.add(initial)
    val walls = map.filterValues { it == wall }
    var i = 0
    var best = Int.MAX_VALUE
    while (queue.isNotEmpty()) {
        val state = queue.poll()
        val mapState = states[(state.minutes + 1) % states.size]

        if (state.minutes >= best) continue
        i++
        if (state.current == end) {
            best = min(best, state.minutes)
        }

        val neighbors = state.current.neighbors()
            .filter {
                it.x in 0 until width && it.y in 0 until height
                        && it !in walls
                        && it !in mapState
            }.map {
                state.copy(
                    current = it,
                    minutes = state.minutes + 1
                )
            }.filter {
                it !in queue
            }

        queue.addAll(neighbors)
        if (state.current !in mapState) {
            state.copy(
                minutes = state.minutes + 1,
            ).takeIf { it !in queue }
                ?.let { queue.add(it) }
        }
    }
    return best
}

private data class MapState(
    val current: Point,
    val end: Point,
    val minutes: Int
) : Comparable<MapState> {
    val distance = abs(end.y - current.y) + abs(end.x - current.x)
    val value = distance + minutes

    override fun compareTo(other: MapState): Int = value.compareTo(other.value)
}