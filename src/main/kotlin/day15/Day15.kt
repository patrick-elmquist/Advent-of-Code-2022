package day15

import common.Input
import common.day
import common.util.Point
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

// answer #1: 4879972
// answer #2: 12525726647448

fun main() {
    day(n = 15) {
        part1(expected = 4879972) { input ->
            val (y, _, sensors) = input.parseSensors()
            val sensorsOnY = sensors.filter { it.position.y == y }.map { it.position.x }.distinct().size
            val beaconsOnY = sensors.filter { it.beacon.y == y }.map { it.beacon.x }.distinct().size
            sensors.mapNotNull { it.getSlice(y = y) }
                .fold(ArrayDeque<IntRange>()) { row, range ->
                    row.addOrMerge(range)
                }
                .sumOf { it.last - it.first + 1 } - sensorsOnY - beaconsOnY
        }
        part1 test 1 expect 26

        part2(expected = 12525726647448L) { input ->
            val (_, max, sensors) = input.parseSensors()

            val min = 0
            val minMaxRange = min..max

            val array = Array(max + 1) { ArrayDeque<IntRange>() }
            for (y in minMaxRange) {
                array[y] = sensors.mapNotNull { it.getSlice(y) }
                    .fold(array[y]) { row, range ->
                        row.addOrMerge(range.first.coerceAtLeast(min)..range.last.coerceAtMost(max))
                    }
            }

            val index = array.indexOfFirst { ranges -> ranges.sumOf { it.last - it.first + 1 } != max + 1 }
            array[index].let { ranges ->
                val sortedBy = ranges.sortedBy { it.first }
                val firstRange = sortedBy.first()
                val x = when {
                    ranges.size == 2 -> firstRange.last + 1
                    firstRange.first != min -> min
                    else -> max
                }
                x.toLong() * 4_000_000L + index.toLong()
            }
        }
        part2 test 1 expect 56000011L
    }
}

private data class ParsedData(val line: Int, val max: Int, val sensors: List<SensorWithBeacon>)

private fun Input.parseSensors(): ParsedData {
    val regex = """Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""".toRegex()
    val line = lines.first().removePrefix("line=").toInt()
    val max = lines.drop(1).first().removePrefix("max=").toInt()
    val sensors = lines.drop(2).mapNotNull {
        regex.matchEntire(it)?.destructured?.let { (x1, y1, x2, y2) -> SensorWithBeacon(Point(x1, y1), Point(x2, y2)) }
    }
    return ParsedData(line, max, sensors)
}

private fun ArrayDeque<IntRange>.addOrMerge(range: IntRange): ArrayDeque<IntRange> {
    var merged = range
    val iterator = iterator()
    iterator.forEach { existing ->
        when {
            merged.first >= existing.first && merged.last <= existing.last -> return this
            merged.first > existing.last || merged.last < existing.first -> Unit
            else -> {
                iterator.remove()
                merged = min(existing.first, merged.first)..max(existing.last, merged.last)
            }
        }
    }
    add(merged)
    return this
}

private data class SensorWithBeacon(val position: Point, val beacon: Point) {
    private val distance = abs(position.x - beacon.x) + abs(position.y - beacon.y)
    private val yRange = position.y - distance..position.y + distance
    fun getSlice(y: Int) =
        if (y in yRange) {
            val offset = abs(position.y - y)
            val start = (position.x - distance + offset)
            val end = (position.x + distance - offset)
            start..end
        } else {
            null
        }
}
