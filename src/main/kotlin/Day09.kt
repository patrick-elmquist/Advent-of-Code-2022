
import day.day
import util.Point
import util.neighbors

// answer #1: 6642
// answer #2: 2765

fun main() {
    day(n = 9) {
        part1(expected = 6642) { input ->
            val motions = input.lines.map(String::toDirectionAndDistance)
            val rope = Array(2) { Point(0, 0) }
            trackTail(rope, motions).size
        }
        part1 test 1 expect 13

        part2(expected = 2765) { input ->
            val motions = input.lines.map(String::toDirectionAndDistance)
            val rope = Array(10) { Point(0, 0) }
            trackTail(rope, motions).size
        }
        part2 test 2 expect 36
    }
}

private fun trackTail(rope: Array<Point>, instructions: List<Pair<String, Int>>): Set<Point> =
    buildSet {
        instructions.forEach { (dir, dist) ->
            repeat(dist) {
                rope[0] = rope.first().moveInDirection(dir)
                for (i in 1 until rope.size) {
                    rope[i] = rope[i].follow(rope[i - 1])
                }
                add(rope.last())
            }
        }
    }

private fun Point.moveInDirection(dir: String) =
    when (dir) {
        "L" -> copy(x = x - 1)
        "R" -> copy(x = x + 1)
        "U" -> copy(y = y - 1)
        "D" -> copy(y = y + 1)
        else -> error("unknown dir:$dir")
    }

private fun Point.follow(head: Point): Point {
    if (this in head.neighbors(diagonal = true, includeSelf = true)) {
        return this
    }

    val offsetX = if (head.x < x) -1 else 1
    val offsetY = if (head.y < y) -1 else 1
    return when {
        head.x == x -> copy(y = y + offsetY)
        head.y == y -> copy(x = x + offsetX)
        else -> Point(x = x + offsetX, y = y + offsetY)
    }
}

private fun String.toDirectionAndDistance(): Pair<String, Int> =
    split(" ").let { (dir, dist) -> dir to dist.toInt() }
