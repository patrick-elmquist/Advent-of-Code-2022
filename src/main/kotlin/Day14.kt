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
            bfs(Point(500, 0), map.keys)
        }
        part2 test 1 expect 93
    }
}

private fun bfs(
    point: Point,
    rocks: Set<Point>,
    check: (Int) -> Boolean = { true }
): Int {
    val settled: MutableSet<Point> = mutableSetOf()
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
        val elements = next.filter {
            it !in settled && it !in rocks && it.y < maxY && it !in added && check(it.x)
        }
        queue.addAll(elements)
        added += elements
        loops++
    }
    return settled.size
}

private fun List<String>.parse(): Map<Point, Tile> {
    val parsed = map { it.split(" -> ") }
        .map { directions ->
            directions.map {
                it.split(",").map(String::toInt).let { (x, y) -> Point(x, y) }
            }
        }
    val map = mutableMapOf<Point, Tile>()
    parsed.forEach { stones ->
        stones.zipWithNext().forEach { (start, end) ->
            (start..end).associateWith { Tile.Rock }
                .let { map.putAll(it) }
        }
    }
    return map
}

private fun solve(map: MutableMap<Point, Tile>): Int {
    val start = Point(500, 0)
    var sand = start
    val maxY = map.keys.maxOf { it.y }
    while (true) {
        if (sand.y >= maxY) return map.count { (_, tile) -> tile == Tile.Settled }

        val bottom = sand.copy(x = sand.x, y = sand.y + 1)
        if (bottom !in map) {
            sand = bottom
            continue
        }

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

private enum class Tile { Rock, Settled }

private operator fun Point.rangeTo(other: Point): List<Point> = when (x) {
    other.x -> (if (y < other.y) y..other.y else other.y..y).map { Point(x, it) }
    else -> (if (x < other.x) x..other.x else other.x..x).map { Point(it, y) }
}
