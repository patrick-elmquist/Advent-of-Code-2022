package day24

import common.Input
import common.day
import common.pointCharMap
import common.util.Point
import common.util.neighbors
import java.util.*
import kotlin.math.abs

// answer #1: 334
// answer #2: 934

fun main() {
    day(n = 24) {
        part1(expected = 334) { input ->
            val map = input.pointCharMap.toMutableMap()

            val (start, end) = input.parseStartAndEnd()

            val wallsAndBlizzards = map.mapValues { listOf(it.value) }
                .filter { it != empty }

            val allMapStates = generateAllMapStates(
                map = wallsAndBlizzards,
                width = input.lines.first().length,
                height = input.lines.size
            )

            solve(
                states = allMapStates,
                start = start,
                end = end,
                map = wallsAndBlizzards
            )
        }
        part1 test 2 expect 18

        part2(expected = 934) { input ->
            val map = input.pointCharMap.toMutableMap()

            val (start, end) = input.parseStartAndEnd()

            val wallsAndBlizzards = map
                .mapValues { listOf(it.value) }
                .filter { it != empty }

            val allBlizzardStates = generateAllMapStates(
                map = wallsAndBlizzards,
                width = input.lines.first().length,
                height = input.lines.size
            )

            val minutesThere = solve(
                states = allBlizzardStates,
                start = start,
                end = end,
                map = wallsAndBlizzards
            )

            val minutesBack = solve(
                minutes = minutesThere,
                states = allBlizzardStates,
                start = end,
                end = start,
                map = wallsAndBlizzards
            )

            solve(
                minutes = minutesBack,
                states = allBlizzardStates,
                start = start,
                end = end,
                map = wallsAndBlizzards
            )
        }
        part2 test 2 expect (18 + 23 + 13)
    }
}

private fun Input.parseStartAndEnd(): Pair<Point, Point> {
    val start = lines.first().indexOfFirst { it == '.' }
        .let { Point(it, 0) }
    val end = lines.last().indexOfFirst { it == '.' }
        .let { Point(it, lines.lastIndex) }
    return start to end
}

private fun generateAllMapStates(
    map: Map<Point, List<Char>>,
    width: Int,
    height: Int
): List<Set<Point>> {
    var current = map
    val initial = current.filterValues { it != wall && it != empty }.keys
    val list = mutableListOf(initial)
    val walls = map.filterValues { it == wall }
    while (true) {
        val moved = current
            .filterValues { it != wall && it != empty }
            .flatMap { (point, list) -> list.map { move(point, it, width, height) to it } }
            .groupBy({ it.first }, { it.second })
        if (moved.keys in list) {
            return list
        } else {
            list += moved.keys
            current = walls + moved
        }
    }
}

private fun move(
    position: Point,
    direction: Char,
    width: Int,
    height: Int
) = when (direction) {
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

private val wall = listOf('#')
private val empty = listOf('.')

private fun solve(
    states: List<Set<Point>>,
    start: Point,
    end: Point,
    map: Map<Point, List<Char>>,
    minutes: Int = 0
): Int {
    val width = 0 until map.maxOf { it.key.x } - map.minOf { it.key.x } + 1
    val height = 0 until map.maxOf { it.key.y } - map.minOf { it.key.y } + 1
    val walls = map.filterValues { it == wall }.keys
    val initial = MapState(
        position = start,
        end = end,
        minutes = minutes
    )
    val queue = PriorityQueue<MapState>()
    queue.add(initial)
    val queueSet = queue.toMutableSet()
    while (queue.isNotEmpty()) {
        val state = queue.poll()
        queueSet.remove(state)

        val nextMinute = state.minutes + 1
        val nextBlizzardState = states[nextMinute % states.size]

        if (state.position == end) return state.minutes

        val neighbors = state.position.neighbors()
            .filter { it.x in width && it.y in height }
            .filter { it !in walls && it !in nextBlizzardState }
            .map { state.copy(position = it, minutes = nextMinute) }
            .filter { it !in queueSet }

        queue.addAll(neighbors)
        queueSet.addAll(neighbors)
        if (state.position !in nextBlizzardState) {
            state.copy(minutes = nextMinute)
                .takeIf { it !in queueSet }
                ?.let {
                    queue.add(it)
                    queueSet.add(it)
                }
        }
    }
    error("somehow broke out without returning")
}

private data class MapState(
    val position: Point,
    val end: Point,
    val minutes: Int
) : Comparable<MapState> {
    val distance = abs(end.y - position.y) + abs(end.x - position.x)
    val distanceAndMinutes = distance + minutes

    override fun compareTo(other: MapState): Int =
        distanceAndMinutes.compareTo(other.distanceAndMinutes)
}
