
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
            val matrix = input.parse()
            val width = matrix.first().size
            val height = matrix.size

            var start: Point? = null
            var end: Point? = null
            input.lines.forEachIndexed { i, row ->
                row.forEachIndexed { j, c ->
                    if (c == 'S') {
                        start = Point(j, i)
                    }
                    if (c == 'E') {
                        end = Point(j, i)
                    }
                }
            }

            extracted(start!!, matrix, width, height, end)
        }
        part1 test 1 expect 31

        part2(expected = 443) { input ->
            val matrix = input.parse()
            val width = matrix.first().size
            val height = matrix.size

            var end: Point? = null
            input.lines.forEachIndexed { i, row ->
                row.forEachIndexed { j, c ->
                    if (c == 'E') {
                        end = Point(j, i)
                    }
                }
            }
            val result = mutableMapOf<Point, Int>()
            input.lines.forEachIndexed { i, row ->
                row.forEachIndexed { j, c ->
                    if (c == 'S' || c == 'a') {
                        val s = Point(j, i)
                        result[s] = extracted(s, matrix, width, height, end)
                    }
                }
            }
            result.values.min()
        }
        part2 test 1 expect 29
    }
}

private fun extracted(
    start: Point,
    matrix: Array<Array<Int>>,
    width: Int,
    height: Int,
    end: Point?
): Int {
    val steps = mutableMapOf(start to 0)
    val queue = LinkedList<Point>()
    queue.add(start)
    while (queue.isNotEmpty()) {
        val p = queue.pop()

        val elevation = matrix[p.y][p.x]
        val step = steps.getValue(p)
        p.neighbors()
            .filter { it.x in 0 until width && it.y in 0 until height }
            .filter {
                val itElevation = matrix[it.y][it.x]
                itElevation <= elevation + 1
            }
            .forEach {
                val oldStep = steps[it]
                if (oldStep == null) {
                    steps[it] = step + 1
                } else {
                    steps[it] = min(step + 1, oldStep)
                }
                if (oldStep != steps.getValue(it)) {
                    queue.add(it)
                }
            }
    }
    return steps[end!!] ?: Int.MAX_VALUE
}

private fun Input.parse(): Array<Array<Int>> =
    lines.map { line ->
        line.map {
            when (it) {
                'S' -> 0
                'E' -> 'z' - 'a'
                else -> it - 'a'
            }
        }.toTypedArray()
    }.toTypedArray()
