
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

        }
        part2 test 1 expect Unit
    }
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

private fun makeMove(head: Point, tail: Point, dir: String): Pair<Point, Point> {
    var head = head
    var tail = tail
    when (dir) {
        "L" -> head = head.copy(x = head.x - 1)
        "R" -> head = head.copy(x = head.x + 1)
        "U" -> head = head.copy(y = head.y - 1)
        "D" -> head = head.copy(y = head.y + 1)
    }

    if (tail in head.neighbors(diagonal = true, includeSelf = true)) return head to tail

    val distanceX = head.x - tail.x
    val distanceY = head.y - tail.y
    tail = when (dir) {
        "L" -> {
            if (head.y == tail.y) {
                tail.copy(x = head.x + 1)
            } else {
                tail.copy(x = head.x + 1, y = tail.y + if (distanceY < 0) -1 else 1)
            }
        }

        "R" -> {
            if (head.y == tail.y) {
                tail.copy(x = head.x - 1)
            } else {
                tail.copy(x = head.x - 1, y = tail.y + if (distanceY < 0) -1 else 1)
            }
        }

        "U" -> {
            if (head.x == tail.x) {
                tail.copy(y = head.y + 1)
            } else {
                tail.copy(y = head.y + 1, x = tail.x + if (distanceX < 0) -1 else 1)
            }
        }

        "D" -> {
            if (head.x == tail.x) {
                tail.copy(y = head.y - 1)
            } else {
                tail.copy(y = head.y - 1, x = tail.x + if (distanceX < 0) -1 else 1)
            }
        }
        else -> error("")
    }
    return head to tail
}