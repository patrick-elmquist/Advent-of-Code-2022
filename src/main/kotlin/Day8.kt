
import day.day
import kotlin.math.max

// answer #1: 1818
// answer #2: 368368

fun main() {
    day(n = 8) {
        part1(expected = 1818) { input ->
            val lines = input.lines
            val grid = lines.map { row -> row.map(Char::digitToInt) }
            val shadows = lines.map { row -> row.map { 0 }.toMutableList() }

            addHorizontalShadows(grid, shadows)
            addVerticalShadows(grid, shadows)

            val totalTrees = grid.size * grid.size
            totalTrees - shadows.sumOf { row -> row.count { it == 4 } }

            grid.flatMapIndexed { y, row ->
                row.mapIndexed { x, treeHeight ->
                    listOf(
                        indicesInInDirection(y, Direction.Decrement, grid.size)
                            .map { y -> grid[y][x] }.scoreIsVisible(treeHeight),
                        indicesInInDirection(y, Direction.Increment, grid.size)
                            .map { y -> grid[y][x] }.scoreIsVisible(treeHeight),
                        indicesInInDirection(x, Direction.Decrement, grid.size)
                            .map { x -> grid[y][x] }.scoreIsVisible(treeHeight),
                        indicesInInDirection(x, Direction.Increment, grid.size)
                            .map { x -> grid[y][x] }.scoreIsVisible(treeHeight)
                    )
                }
            }.count {
                it.any { it }
            }
        }
        part1 test 1 expect 21

        part2(expected = 368368) { input ->
            val grid = input.lines.map { row -> row.map(Char::digitToInt) }
            grid.flatMapIndexed { y, row ->
                row.mapIndexed { x, treeHeight ->
                    listOf(
                        indicesInInDirection(y, Direction.Decrement, grid.size)
                            .map { y -> grid[y][x] }.score(treeHeight, y),
                        indicesInInDirection(y, Direction.Increment, grid.size)
                            .map { y -> grid[y][x] }.score(treeHeight, grid.lastIndex - y),
                        indicesInInDirection(x, Direction.Decrement, grid.size)
                            .map { x -> grid[y][x] }.score(treeHeight, x),
                        indicesInInDirection(x, Direction.Increment, grid.size)
                            .map { x -> grid[y][x] }.score(treeHeight, grid.lastIndex - x)
                    ).reduce(Int::times)
                }
            }.max()
        }
        part2 test 1 expect 8
    }
}

private fun addVerticalShadows(
    grid: List<List<Int>>,
    shadow: List<MutableList<Int>>
) {
    var tallestDown: Int
    var tallestUp: Int
    grid.forEachIndexed { index, row ->

    }
    for (x in grid.indices) {
        tallestDown = grid[0][x]
        tallestUp = grid[grid.size - 1][x]
        for (y in 1 until grid.size) {
            val treeHeight = grid[y][x]
            if (treeHeight <= tallestDown) shadow.increment(x, y)
            tallestDown = max(tallestDown, treeHeight)

            val treeHeight2 = grid[grid.size - 1 - y][x]
            if (treeHeight2 <= tallestUp) shadow.increment(x, grid.size - 1 - y)
            tallestUp = max(tallestUp, treeHeight2)
        }
    }
}

private fun addHorizontalShadows(
    grid: List<List<Int>>,
    shadow: List<MutableList<Int>>
) {
    grid.forEachIndexed { y, row ->
        var tallest = row.first()
        row.withIndex().drop(1).forEach { (x, treeHeight) ->
            if (treeHeight <= tallest) shadow.increment(x, y)
            tallest = max(tallest, treeHeight)
        }
        tallest = row.last()
        row.withIndex().reversed().drop(1).forEach { (x, treeHeight) ->
            if (treeHeight <= tallest) shadow.increment(x, y)
            tallest = max(tallest, treeHeight)
        }
    }
}

private fun List<MutableList<Int>>.increment(x: Int, y: Int) {
    this[y][x] = this[y][x] + 1
}

private fun Sequence<Int>.scoreIsVisible(height: Int)
    = if (none()) true else !any { it >= height }

private fun Sequence<Int>.score(height: Int, max: Int): Int {
    if (none()) return 0
    val score = takeWhile { it < height }.count()
    return if (score == max) {
        score
    } else {
        score + 1
    }
}

private enum class Direction { Decrement, Increment}

private fun indicesInInDirection(value: Int, direction: Direction, height: Int) =
    when (direction) {
        Direction.Increment -> if (value == height - 1) emptySequence() else (value + 1 until height).asSequence()
        Direction.Decrement -> if (value == 0) emptySequence() else (value - 1 downTo 0).asSequence()
    }
