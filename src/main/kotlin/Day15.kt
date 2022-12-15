
import day.day
import util.Point
import util.log
import util.loggingEnabled
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

// answer #1: 4879972
// answer #2: 12525726647448

fun main() {
    day(n = 15) {
//        ignorePart1 = true
        val pattern = """Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""".toRegex()
        part1(expected = 4879972) { input ->
            val lineToLookFor = 2000000
            val circles = input.lines.mapNotNull {
                pattern.matchEntire(it)?.destructured?.let { (x1, y1, x2, y2) ->
                    Circle(Point(x1, y1), Point(x2, y2))
                }
            }
            val centersOnLine = circles.filter { it.center.y == lineToLookFor }.map { it.center.x }
            val pointsOnLine = circles.filter { it.point.y == lineToLookFor }.map { it.point.x }
            val toLookAt = circles.filter { lineToLookFor in it.yrange }
            val setOfX = mutableSetOf<Int>()
            toLookAt.forEach {
                val dist = abs(it.center.y - lineToLookFor)
                val start = it.radius - dist
                setOfX.addAll(it.center.x - start..it.center.x + start)
            }
            setOfX.filter {
                it !in centersOnLine && it !in pointsOnLine
            }.size
        }
//        part1 test 1 expect 26

        part2 { input ->
            val circles = input.lines.mapNotNull {
                pattern.matchEntire(it)?.destructured?.let { (x1, y1, x2, y2) ->
                    Circle(Point(x1, y1), Point(x2, y2))
                }
            }

//            circles.first().widthPerRow().sortedBy { it.first }.forEach { (y, range) ->
//                for (i in (-5..9)) {
//                    if (i in range) {
//                        print('#')
//                    } else {
//                        print(' ')
//                    }
//                }
//                println()
//            }
//            circles.first().widthPerRow().toList().log()

            val min = 0
            val max = 4_000_000

            loggingEnabled = false
            val map = mutableMapOf<Int, MutableList<IntRange>>()
            circles.forEach { circle ->
                circle.widthPerRow().forEach loop@{ (y, range) ->
                    if (y < min || y > max) return@loop
                    val row = map.get(y)
                    val adjusted = range.first.coerceAtLeast(min)..range.last.coerceAtMost(max)
                    if (row == null) {
                        map[y] = mutableListOf(adjusted)
                    } else {
                        map[y] = mergeRanges(adjusted, row, min, max).log("from merged")
                    }
                }
            }
            map.entries.onEach { (y, ranges) ->
//                ranges.log("$y")
            }.first { (y, ranges) ->
                ranges.sumOf { it.last - it.first + 1 } != max + 1
            }
                .let { (y, ranges) ->
                    val sortedBy = ranges.sortedBy { it.first }
                    val x = if (ranges.size == 2) {
                        sortedBy.first().last + 1
                    } else if (sortedBy.first().first != min) {
                        min
                    } else {
                        max
                    }
                    x.toLong() * 4_000_000L + y.toLong()
                }
        }
//        part2 test 1 expect 56000011L
    }
}

fun mergeRanges(rin: IntRange, row: MutableList<IntRange>, min: Int, max: Int): MutableList<IntRange> {
    val out = mutableListOf<IntRange>()
    var r = rin.log("rin")
    row.forEach { x ->
        when {
            r.first >= x.first && r.last <= x.last -> {
                // r is fully within, quick return
                return row.log("quick")
            }

            r.first > x.last || r.last < x.first -> {
                // r is fully outside
                out.add(x)
                x.log("adding x")
            }
//            r.last < min || r.first < max -> {
//                return row
//            }
            else -> {
                val start = min(x.first, r.first).coerceAtLeast(min)
                val end = max(x.last, r.last).coerceAtMost(max)
                r = start..end
                r.log("new r")
            }
        }
    }
    out += r
    return out
}

private data class Circle(val center: Point, val point: Point) {
    val radius = center.manhattanDistance(point)
    val xrange = center.x - radius..center.x + radius
    val yrange = center.y - radius..center.y + radius

    fun widthPerRow(): Sequence<Pair<Int, IntRange>> {
        return sequence {
            yield(center.y to center.x - radius..center.x + radius)
            for (y in (1..radius)) {
                val start = (center.x - radius + y)
                val end = (center.x + radius - y)
                yield(center.y + y to start..end)
                yield(center.y - y to start..end)
            }
        }
    }
}

private fun Point.manhattanDistance(other: Point) =
    abs(x - other.x) + abs(y - other.y)