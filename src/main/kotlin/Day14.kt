
import day.day
import util.Point
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

// answer #1: 1406
// answer #2: 20870

fun main() {
    day(n = 14) {
        part1(expected = 1406) { input ->
            val map = input.lines.parse()
            solve(map.toMutableMap())
        }
        part1 test 1 expect 24

        part2(expected = 20870) { input ->
            val map = input.lines.parse()
//            solve2(map.toMutableMap()).log()
            val settled = mutableSetOf<Point>()
            bfs(
                Point(500, 0),
                settled,
                map.keys
            )
            settled.size
        }
        part2 test 1 expect 93
    }
}

private fun bfs(
    point: Point,
    settled: MutableSet<Point>,
    rocks: Set<Point>
) {
    val queue = ArrayDeque<Point>()
    val added = mutableSetOf<Point>()
    queue.add(point)
    val maxY = rocks.maxOf { it.y } + 2
    var loops = 0
    while (queue.isNotEmpty()) {
        val p = queue.removeFirst()
        val next = buildList {
            with(p) {
                add(copy(y = y + 1))
                add(copy(x = x - 1, y = y + 1))
                add(copy(x = x + 1, y = y + 1))
            }
        }

        settled += p
        val elements = next.filter { it !in settled && it !in rocks && it.y < maxY && it !in added }
        queue.addAll(elements)
        added += elements
//        queue.log()
        loops++
//        if (loops == 3) TODO()
    }
}

private fun List<String>.parse(): Map<Point, Tile> {
    val parsed = map { it.split(" -> ") }
        .map {
            it.map {
                val (x, y) = it.split(",").map(String::toInt)
                Point(x, y)
            }
        }
    return createMapWithStones(parsed)
}

private fun solve2(map: MutableMap<Point, Tile>): Int {
    val start = Point(500, 0)
    var sand = start
    val maxY = map.maxOf { it.key.y }
    val floor = maxY + 2
    while (map[start] != Tile.Settled) {
        val below2 = map.findBelow(sand)
        val below = below2
            ?: if (map[start] == Tile.Settled) {
                return map.count { (_, tile) -> tile == Tile.Settled }
            } else {
                Point(x = sand.x, y = floor)
            }

        val left = below.copy(x = sand.x - 1)
        if (left !in map && left.y < floor) {
            sand = left
            continue
        }

        val right = below.copy(x = sand.x + 1)
        if (right !in map && right.y < floor) {
            sand = right
            continue
        }

        map[below.copy(y = below.y - 1)] = Tile.Settled
        sand = start
    }
    return map.count { (_, tile) -> tile == Tile.Settled }
}

private fun solve(map: MutableMap<Point, Tile>): Int {
    val start = Point(500, 0)
    var sand = start
    while (true) {
        val below = map.findBelow(sand) ?: return map.count { (_, tile) -> tile == Tile.Settled }
        sand = below.copy(y = below.y - 1)

        val left = sand.copy(x = sand.x - 1, y = sand.y + 1)
        if (left !in map) {
            sand = left
            continue
        }

        val right = sand.copy(x = sand.x + 1, y = sand.y + 1)
        if (right !in map) {
            sand = right
            continue
        }

        map[sand] = Tile.Settled
        sand = start
    }
}

private fun Map<Point, Tile>.findBelow(point: Point): Point? {
    return filterKeys { it.x == point.x }
        .filterKeys { it.y > point.y }
        .minByOrNull { it.key.y }
        ?.key
}

private enum class Tile { Rock, Settled, Sand }

private fun createMapWithStones(parsed: List<List<Point>>): Map<Point, Tile> {
    val map = mutableMapOf<Point, Tile>()
    parsed.forEach { stones ->
        stones.zipWithNext().forEach { (start, end) ->
            start.pointsTo(end).associateWith { Tile.Rock }
                .let { map.putAll(it) }
        }
    }
    return map
}

private fun Point.pointsTo(other: Point): List<Point> {
    if (this == other) return emptyList()
    return if (x == other.x) {
        val range = if (y < other.y) {
            y..other.y
        } else {
            other.y..y
        }
        range.map { Point(x, it) }
    } else {
        val range = if (x < other.x) {
            x..other.x
        } else {
            other.x..x
        }
        range.map { Point(it, y) }
    }
}

private fun Map<Point, Tile>.print(sleep: Boolean = false) {
    if (sleep) {
//        Thread.sleep(500L)
//        return
    } else {
        return
    }
    val minX = minOf { it.key.x }
    val maxX = maxOf { it.key.x }
    val minY = minOf { it.key.y }.coerceAtMost(0)
    val maxY = maxOf { it.key.y }
    for (i in minY - 1..maxY + 1) {
        for (j in minX - 1..maxX + 1) {
            val p = Point(j, i)
            val c = when (get(p)) {
                null -> {
                    if (p.x == 500 && p.y == 0) {
                        '+'
                    } else {
                        '.'
                    }
                }

                Tile.Rock -> '#'
                Tile.Settled -> 'O'
                Tile.Sand -> '@'
            }
            print(c)
        }
        println()
    }
    println()
}

