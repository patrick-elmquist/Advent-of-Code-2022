
import day.Input
import day.day
import util.Point
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

// answer #1: 4879972
// answer #2: 12525726647448

fun main() {
    day(n = 15) {
        part1(expected = 4879972) { input ->
            val (lineToLookFor, _, sensors) = input.parseSensors()
            val centersOnLine = sensors.filter { it.position.y == lineToLookFor }.map { it.position.x }.distinct().size
            val pointsOnLine = sensors.filter { it.beacon.y == lineToLookFor }.map { it.beacon.x }.distinct().size
            val mapRow = ArrayDeque<IntRange>()
            sensors
                .mapNotNull { it.getSlice(y = lineToLookFor) }
                .forEach { range ->
                    val adjusted = range.first..range.last
                    mergeRanges(adjusted, mapRow)
                }

            mapRow.sumOf { it.last - it.first + 1 } - centersOnLine - pointsOnLine
        }
        part1 test 1 expect 26

        part2(expected = 12525726647448L) { input ->
            val (_, max, sensors) = input.parseSensors()

            val min = 0
            val minMaxRange = min..max

            val array = Array(max + 1) { ArrayDeque<IntRange>() }
            for (y in minMaxRange) {
                sensors.forEach {
                    it.getSlice(y)?.let { slice ->
                        val adjusted = slice.first.coerceAtLeast(min)..slice.last.coerceAtMost(max)
                        mergeRanges(adjusted, array[y])
                    }
                }
            }

            val index = array.indexOfFirst { ranges -> ranges.sumOf { it.last - it.first + 1 } != max + 1 }
            array[index].let { ranges ->
                val sortedBy = ranges.sortedBy { it.first }
                val x = if (ranges.size == 2) {
                    sortedBy.first().last + 1
                } else if (sortedBy.first().first != min) {
                    min
                } else {
                    max
                }
                x.toLong() * 4_000_000L + index.toLong()
            }
        }
        part2 test 1 expect 56000011L
    }
}

private data class ParsedData(val line: Int, val max: Int, val sensors: List<Sensor>)

private fun Input.parseSensors(): ParsedData {
    val line = lines.first().removePrefix("line=").toInt()
    val max = lines.drop(1).first().removePrefix("max=").toInt()
    val regex = """Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""".toRegex()
    val sensors = lines.drop(2).mapNotNull {
        regex.matchEntire(it)?.destructured?.let { (x1, y1, x2, y2) ->
            Sensor(Point(x1, y1), Point(x2, y2))
        }
    }
    return ParsedData(line, max, sensors)
}

private fun mergeRanges(rangeToAdd: IntRange, row: ArrayDeque<IntRange>): ArrayDeque<IntRange> {
    var range = rangeToAdd
    val iterator = row.iterator()
    iterator.forEach { x ->
        when {
            range.first >= x.first && range.last <= x.last -> return row
            range.first > x.last || range.last < x.first -> Unit
            else -> {
                iterator.remove()
                range = min(x.first, range.first)..max(x.last, range.last)
            }
        }
    }
    row += range
    return row
}

private data class Sensor(val position: Point, val beacon: Point) {
    val radius = position.manhattanDistance(beacon)
    fun getSlice(y: Int): IntRange? {
        return if (y < position.y - radius || y > position.y + radius) {
            null
        } else {
            val diff = abs(position.y - y)
            val start = (position.x - radius + diff)
            val end = (position.x + radius - diff)
            start..end
        }
    }
}

private fun Point.manhattanDistance(other: Point) =
    abs(x - other.x) + abs(y - other.y)