
import day.day
import util.Point
import util.neighbors

// answer #1: 6642
// answer #2: 2765

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

            printRope(listOf(head, tail))
            visited.size
        }
        part1 test 1 expect 13

        part2(expected = 2765) { input ->
            val instructions = input.lines.map {
                val (dir, dist) = it.split(" ")
                dir to dist.toInt()
            }

            var rope = MutableList(10) { Point(0, 0) }
            val visited = mutableSetOf(Point(0, 0))
            printRope(rope)
            instructions.forEach { (dir, dist) ->
                println("== $dir $dist ==")
                repeat(dist) {
//                    printRope(rope)
                    rope[0] = moveSingle(dir, rope.first())

//                    printRope(rope)
                    for (i in 1 until rope.size) {
                        val prev = rope[i - 1]
                        val current = rope[i]
                        rope[i] = moveBasedOn(head = prev, tail = current)
                    }
//                    println()
//                    println()
                    visited += rope.last()
                }
                printRope(rope)
            }

            visited.size
        }
        part2 test 2 expect 36
    }
}

private fun printRope(rope: List<Point>) {
//    val minX = rope.minOf { it.x }
//    val maxX = rope.maxOf { it.x }
//    val minY = rope.minOf { it.y }
//    val maxY = rope.maxOf { it.y }
    val minX = -11
    val maxX = 14
    val minY = -15
    val maxY = 5
    for (y in minY..maxY) {
        for (x in minX..maxX) {
            val p = rope.indexOf(Point(x, y))
            if (p > -1) {
                val out = when (p) {
                    0 -> 'H'
//                    rope.lastIndex -> 'T'
                    else -> p
                }
                print("$out")
            } else {
                if (x == 0 && y == 0) {
                    print("s")
                } else {
                    print(".")
                }
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
//        printRope(listOf(head, tail))
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

private fun moveBasedOn(tail: Point, head: Point): Point {
    val distanceX = head.x - tail.x
    val distanceY = head.y - tail.y
    if (tail in head.neighbors(diagonal = true, includeSelf = true)) return tail
    return if (head.x == tail.x) {
        tail.copy(y = head.y + if (distanceY < 0) 1 else -1)
    } else if (head.y == tail.y) {
        tail.copy(x = head.x + if (distanceX < 0) 1 else -1)
    } else {
        tail.copy(x = tail.x + if (distanceX < 0) -1 else 1, y = tail.y + if (distanceY < 0) -1 else 1)
    }
}

private fun makeMove(head: Point, tail: Point, dir: String): Pair<Point, Point> {
    val head = moveSingle(dir, head)

    if (tail in head.neighbors(diagonal = true, includeSelf = true)) return head to tail

    val tail = moveBasedOn(tail = tail, head = head)
    return head to tail
}