package day23

import common.day
import common.pointCharMap
import common.util.Point
import common.util.neighbors

// answer #1: 4052
// answer #2: 978

fun main() {
    day(n = 23) {
        part1(expected = 4052) { input ->
            val startPositions = input.pointCharMap.filterValues { it == '#' }.keys
            val (elves, _) = solve(startPositions, rounds = 10)
            val width = elves.maxOf { it.x } - elves.minOf { it.x } + 1
            val height = elves.maxOf { it.y } - elves.minOf { it.y } + 1
            width * height - elves.size
        }
        part1 test 1 expect 110
        part1 test 2 expect 25

        part2(expected = 978) { input ->
            val startPositions = input.pointCharMap.filterValues { it == '#' }.keys
            val (_, round) = solve(startPositions, rounds = Int.MAX_VALUE)
            round
        }
        part2 test 1 expect 20
    }
}

private val directions = listOf(
    listOf(Point(0, -1), Point(1, -1), Point(-1, -1)),  // N, NE, NW
    listOf(Point(0, 1), Point(1, 1), Point(-1, 1)),     // S, SE, SW
    listOf(Point(-1, 0), Point(-1, -1), Point(-1, 1)),  // W, NW, SW
    listOf(Point(1, 0), Point(1, -1), Point(1, 1))      // E, NE, SE
)

private fun solve(startingPositions: Set<Point>, rounds: Int): Pair<Set<Point>, Int> {
    var elves = startingPositions

    repeat(times = rounds) { i ->
        val proposedMovements = elves.map { from ->
            val to = if (from.neighbors(diagonal = true).none { it in elves }) {
                from
            } else {
                val offset = directions.indices.map { directions[(it + i) % directions.size] }
                    .firstOrNull { dirs -> dirs.none { from + it in elves } }
                    ?.first()
                    ?: Point(0, 0)
                from + offset
            }
            from to to
        }

        val elvesPerCell = proposedMovements.groupingBy { (_, to) -> to }.eachCount()

        elves = proposedMovements
            .map { (from, to) -> to.takeIf { elvesPerCell[it] == 1 } ?: from }
            .toSet()
            .takeIf { it != elves } ?: return elves to i + 1
    }

    return elves to rounds
}
