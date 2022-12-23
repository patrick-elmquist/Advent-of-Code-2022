import Compass.*
import day.Input
import day.day
import util.Point
import util.neighbors


// answer #1: 4052
// answer #2:

fun main() {
    day(n = 23) {
        part1(expected = 4052) { input ->
            val (elfs, _) = solve(parseElfs(input), rounds = 10)
            elfs.minMax { it.x }.count() * elfs.minMax { it.y }.count() - elfs.size
        }
        part1 test 2 expect 25
        part1 test 1 expect 110

        part2(expected = 978) { input ->
            solve(parseElfs(input), rounds = Int.MAX_VALUE).second
        }
        part2 test 1 expect 20
    }
}

private fun parseElfs(input: Input): Set<Point> =
    input.lines
        .flatMapIndexed { y, row -> row.mapIndexed { x, c -> Point(x, y) to c } }
        .toMap()
        .filterValues { it == '#' }
        .keys

private enum class Compass(val point: Point) {
    N(Point(0, -1)),
    E(Point(1, 0)),
    S(Point(0, 1)),
    W(Point(-1, 0)),
    NW(Point(-1, -1)),
    NE(Point(1, -1)),
    SE(Point(1, 1)),
    SW(Point(-1, 1))
}

private fun direction(round: Int): List<Point> =
    when (round % 4) {
        0 -> listOf(N, NE, NW)
        1 -> listOf(S, SE, SW)
        2 -> listOf(W, NW, SW)
        3 -> listOf(E, NE, SE)
        else -> error("")
    }.map { it.point }

private fun solve(elfs: Set<Point>, rounds: Int): Pair<Set<Point>, Int> {
    var e = elfs

    repeat(times = rounds) { i ->
        val newPositions = e
            .map { elf ->
                val n = elf.neighbors(diagonal = true)
                if (n.none { it in e }) {
                    elf to elf
                } else {
                    elf to elf + ((0 until 4).asSequence()
                        .map { direction(it + i) }
                        .firstOrNull { dirs -> dirs.none { elf + it in e } }
                        ?.first() ?: Point(0, 0))
                }
            }

        val count = newPositions.groupingBy { it.second }.eachCount()
        val newE = newPositions.map {
            if (count.getValue(it.second) == 1) {
                it.second
            } else {
                it.first
            }
        }.toSet()
        if ((e - newE).isEmpty()) return e to i + 1
        e = newE
    }
    return e to rounds
}

private fun Set<Point>.minMax(block: (Point) -> Int): IntRange =
    minOf { block(it) }..maxOf { block(it) }
