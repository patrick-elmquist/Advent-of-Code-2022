import day.day
import util.Point
import util.neighbors

// answer #1: 6642
// answer #2:

fun main() {
    day(n = 9) {
        part1 { input ->
            val instructions = input.lines.map {
                val (dir, dist) = it.split(" ")
                dir to dist.toInt()
            }

            var head = Point(0, 0)
            var tail = Point(0, 0)
            val visited = mutableSetOf(tail)
            instructions.forEach { (dir, dist) ->
                val move = makeMove(head, tail, dir, dist, visited)
                head = move.first
                tail = move.second
            }

            visited.size
        }
        part1 test 1 expect 13

        part2 { input ->
            val instructions = input.lines.map {
                val (dir, dist) = it.split(" ")
                dir to dist.toInt()
            }

            var rope = List(10) { Point(0, 0) }
            val visited = mutableSetOf(Point(0, 0))
            instructions.forEach { (dir, dist) ->
                repeat(dist) {
                    printRope(rope)
                    var head = rope.first()
                    head = moveSingle(dir, head)
                    rope = listOf(head) + rope.drop(1)
                    printRope(rope)
                    rope = rope.windowed(2) { (head, tail) ->
                        moveBasedOn(dir, head = head, tail = tail)
                    }
                    visited += rope.last()
                }
            }

            visited.size
        }
        part2 test 1 expect 36
    }
}

private fun printRope(rope: List<Point>) {
    val minX = rope.minOf { it.x }
    val maxX = rope.maxOf { it.x }
    val minY = rope.minOf { it.y }
    val maxY = rope.maxOf { it.y }
    for (y in minY..maxY) {
        for (x in minX..maxX) {
            val p = rope.indexOf(Point(x, y))
            if (p > -1) {
                val out = when(p) {
                    0 -> 'H'
                    9 -> 'T'
                    else -> p
                }
                print("$out")
            } else {
                print(".")
            }
        }
        println()
    }
    println()
}

private fun makeMove(head: Point, tail: Point, dir: String, dist: Int, visited: MutableSet<Point>): Pair<Point, Point> {
    var head = head
    var tail = tail
    repeat(dist) {
        val (newHead, newTail) = makeMove(head, tail, dir)
        head = newHead
        tail = newTail
        visited += tail
    }
    return head to tail
}

private fun moveSingle(dir: String, point: Point): Point {
    return when (dir) {
        "L" -> point.copy(x = point.x - 1)
        "R" -> point.copy(x = point.x + 1)
        "U" -> point.copy(y = point.y - 1)
        "D" -> point.copy(y = point.y + 1)
        else -> error("")
    }
}

private fun moveBasedOn(dir: String, tail: Point, head: Point): Pair<String, Point> {
    val distanceX = head.x - tail.x
    val distanceY = head.y - tail.y
    return when (dir) {
        "L" -> {
            if (head.y == tail.y) {
                "L" to tail.copy(x = head.x + 1)
            } else {
                tail.copy(x = head.x + 1, y = tail.y + if (distanceY < 0) -1 else 1)
            }
        }

        "R" -> {
            if (head.y == tail.y) {
                "R" to tail.copy(x = head.x - 1)
            } else {
                tail.copy(x = head.x - 1, y = tail.y + if (distanceY < 0) -1 else 1)
            }
        }

        "U" -> {
            if (head.x == tail.x) {
                "U" to tail.copy(y = head.y + 1)
            } else {
                tail.copy(y = head.y + 1, x = tail.x + if (distanceX < 0) -1 else 1)
            }
        }

        "D" -> {
            if (head.x == tail.x) {
                "D" to tail.copy(y = head.y - 1)
            } else {
                tail.copy(y = head.y - 1, x = tail.x + if (distanceX < 0) -1 else 1)
            }
        }

        else -> error("")
    }
}

private fun makeMove(head: Point, tail: Point, dir: String): Pair<Point, Point> {
    val head = moveSingle(dir, head)

    if (tail in head.neighbors(diagonal = true, includeSelf = true)) return head to tail

    val (_, tail) = moveBasedOn(dir, tail = tail, head = head)
    return head to tail
}