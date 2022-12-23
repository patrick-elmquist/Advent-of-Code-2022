import day.Input
import day.day
import util.Point
import util.neighbors

// answer #1: 4052
// answer #2: 978

fun main() {
    day(n = 23) {
        part1(expected = 4052) { input ->
            val (elves, _) = solve(parseElves(input), rounds = 10)
            val width = elves.maxOf { it.x } - elves.minOf { it.x } + 1
            val height = elves.maxOf { it.y } - elves.minOf { it.y } + 1
            width * height - elves.size
        }
        part1 test 1 expect 110
        part1 test 2 expect 25

        part2(expected = 978) { input ->
            solve(parseElves(input), rounds = Int.MAX_VALUE).second
        }
        part2 test 1 expect 20
    }
}

private fun parseElves(input: Input): Set<Point> =
    input.lines
        .flatMapIndexed { y, row -> row.mapIndexed { x, c -> Point(x, y) to c } }
        .toMap()
        .filterValues { it == '#' }
        .keys

private val directions = listOf(
    listOf(Point(0, -1), Point(1, -1), Point(-1, -1)),
    listOf(Point(0, 1), Point(1, 1), Point(-1, 1)),
    listOf(Point(-1, 0), Point(-1, -1), Point(-1, 1)),
    listOf(Point(1, 0), Point(1, -1), Point(1, 1))
)

private fun solve(startingPositions: Set<Point>, rounds: Int): Pair<Set<Point>, Int> {
    var elves = startingPositions

    repeat(times = rounds) { i ->
        val proposedMovements = elves.map { from ->
            val to = if (from.neighbors(diagonal = true).none { it in elves }) {
                from
            } else {
                from + ((0 until 4).map { directions[(it + i) % directions.size] }
                    .firstOrNull { dirs -> dirs.none { from + it in elves } }
                    ?.first() ?: Point(0, 0))
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
