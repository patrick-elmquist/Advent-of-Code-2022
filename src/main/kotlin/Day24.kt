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

fun main() {
    day(n = 24) {
        part1(expected = 334) { input ->
            val map = input.pointCharMap.toMutableMap()

            val width = input.lines.first().length - 2
            val height = input.lines.size - 2

            val start = input.lines.first().indexOfFirst { it == '.' }
                .let { Point(it, 0) }
            val end = input.lines.last().indexOfFirst { it == '.' }
                .let { Point(it, input.lines.lastIndex) }

            val filtered = map.mapValues { listOf(it.value) }
                .filter { it != empty }
            val states = generateMaps(
                map = filtered,
                width = input.lines.first().length,
                height = input.lines.size
            )

            solve(
                minutes = 0,
                states = states,
                start = start,
                end = end,
                map = filtered,
                width = input.lines.first().length,
                height = input.lines.size
            )
        }
        part1 test 2 expect 18

        part2(expected = 934) { input ->
            val map = input.pointCharMap.toMutableMap()

            val width = input.lines.first().length
            val height = input.lines.size

            val start = input.lines.first().indexOfFirst { it == '.' }
                .let { Point(it, 0) }
            val end = input.lines.last().indexOfFirst { it == '.' }
                .let { Point(it, input.lines.lastIndex) }

            // Don't filter away the values, keep them around and filter when moving the blizzards instead
            // Right now we check neighbors that can be on the border and that's invalid
            val filtered = map.mapValues { listOf(it.value) }
                .filter { it != empty }
            val states = generateMaps(
                map = filtered,
                width = input.lines.first().length,
                height = input.lines.size
            )

            val minutesThere = solve(
                minutes = 0,
                states = states,
                start = start,
                end = end,
                map = filtered,
                width = width,
                height = height
            )
            val minutesBack= solve(
                minutes = minutesThere,
                states = states,
                start = end,
                end = start,
                map = filtered,
                width = width,
                height = height
            )
            val minutesThereAgain = solve(
                minutes = minutesBack,
                states = states,
                start = start,
                end = end,
                map = filtered,
                width = width,
                height = height
            )
            minutesThereAgain
        }
        part2 test 2 expect (18 + 23 + 13)
    }
}

private fun generateMaps(
    map: Map<Point, List<Char>>,
    width: Int,
    height: Int
): List<Map<Point, Char>> {
    var start = map
    var blizzards = start.filterValues { it != wall && it != empty }
    var flattened = blizzards.mapValues { '@' }
    val list = mutableListOf<Map<Point, Char>>()
    val sets = mutableSetOf<Map<Point, Char>>()
    val walls = map.filterValues { it == wall }
    sets += flattened
    list += flattened
    var i = 0
    while (true) {
        blizzards = start.filterValues { it != wall && it != empty }
        val moved = blizzards
            .flatMap { (point, list) -> list.map { move(point, it, width, height) to it } }
            .groupBy({ it.first }, { it.second })
        flattened = moved.mapValues { '@' }
        i++
        if (flattened in sets) {
            "generate finished after $i rounds".log()
            return list
        } else {
            sets += flattened
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
    minutes: Int,
    states: List<Map<Point, Char>>,
    start: Point,
    end: Point,
    map: Map<Point, List<Char>>,
    width: Int,
    height: Int
): Int {
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