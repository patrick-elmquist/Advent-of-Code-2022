import Compass.*
import day.day
import util.Point
import util.neighbors


// answer #1: 4052
// answer #2:

fun main() {
    day(n = 23) {
        part1(expected = 4052) { input ->
            val elfs = input.lines.flatMapIndexed { y, row ->
                row.mapIndexed { x, c ->
                    Point(x, y) to c
                }
            }
                .toMap()
                .filterValues { it == '#' }
                .keys

            elfs.print()

            val newSet = solve(elfs, rounds = 10)
            val minMaxX = newSet.minMax { it.x }
            val minMaxY = newSet.minMax { it.y }
            minMaxX.count() * minMaxY.count() - newSet.size
        }
        part1 test 2 expect 25
        part1 test 1 expect 110

        part2 { input ->

        }
        part2 test 1 expect 1
    }
}


private enum class Compass(val p: Point) {
    N(Point(0, -1)),
    E(Point(1, 0)),
    S(Point(0, 1)),
    W(Point(-1, 0)),
    NW(Point(-1, -1)),
    NE(Point(1, -1)),
    SE(Point(1, 1)),
    SW(Point(-1, 1))
}


private fun direction(round: Int): List<Point> {
    return when (round % 4) {
        0 -> listOf(N, NE, NW)
        1 -> listOf(S, SE, SW)
        2 -> listOf(W, NW, SW)
        3 -> listOf(E, NE, SE)
        else -> error("")
    }.map { it.p }
}

private fun solve(elfs: Set<Point>, rounds: Int): Set<Point> {
    var e = elfs
    var i = 0

    repeat(times = rounds) {
        val newPositions = e
            .map { elf ->
                val n = elf.neighbors(diagonal = true)
                if (n.none { it in e }) {
                    elf to elf
                } else {
                    elf to elf + ((0 until 4).asSequence()
                        .map { direction(it + i) }
                        .firstOrNull { dirs -> dirs.none { elf + it in e } }
                        ?.first() ?: Point(0,0))
                }
            }

        val count = newPositions.groupingBy { it.second }.eachCount()
        e = newPositions.map {
            if (count.getValue(it.second) == 1) {
                it.second
            } else {
                it.first
            }
        }.toSet()
        e.print()
        println()
        i++
    }
    return e
}

private fun Set<Point>.minMax(block: (Point) -> Int): IntRange {
    val minx = minOf { block(it) }
    val maxx = maxOf { block(it) }
    return minx..maxx
}

private fun Set<Point>.print() {
    val minMaxX = minMax { it.x }
    val minMaxY = minMax { it.y }
    for (y in minMaxY) {
        for (x in minMaxX) {
            if (Point(x, y) in this) {
                print('#')
            } else {
                print('.')
            }
        }
        println()
    }
    println()
}