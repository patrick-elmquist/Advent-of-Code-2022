
import day.day
import kotlin.math.max

// answer #1: 1818
// answer #2: 368368

fun main() {
    day(n = 8) {
        part1(expected = 1818) { input ->
            val grid = input.lines.mapIndexed { y, row ->
                row.mapIndexed { x, height ->
                    height.digitToInt()
                }
            }

            val shadows = input.lines.map { row -> row.map { 0 }.toMutableList() }

            val width = input.lines.first().count()
            val height = input.lines.count()

            addHorizontalShadows(grid, shadows)
            addVerticalShadows(width, grid, height, shadows)

            val totalTrees = width * height
            totalTrees - shadows.sumOf { row -> row.count { it == 4 }}
        }
        part1 test 1 expect 21

        part2(expected = 368368) { input ->
            val grid = input.lines.map { row -> row.map { it.digitToInt() } }
            val width = input.lines.first().count()
            val height = input.lines.count()
            grid.flatMapIndexed { y, row ->
                row.mapIndexed { x, treeHeight ->
                    val up = getAllInDirection(y, Operation.Decrement, height)
                        .map { y -> grid[y][x] }.score(treeHeight, y)
                    val down = getAllInDirection(y, Operation.Increment, height)
                        .map { y -> grid[y][x] }.score(treeHeight, height - 1 - y)
                    val left = getAllInDirection(x, Operation.Decrement, width)
                        .map { x -> grid[y][x] }.score(treeHeight, x)
                    val right = getAllInDirection(x, Operation.Increment, width)
                        .map { x -> grid[y][x] }.score(treeHeight, width - 1 - x)
                    up * down * left * right
                }
            }.max()
        }
        part2 test 1 expect 8
    }
}

private fun addVerticalShadows(
    width: Int,
    grid: List<List<Int>>,
    height: Int,
    shadow: List<MutableList<Int>>
) {
    var tallestDown: Int
    var tallestUp: Int
    for (x in 0 until width) {
        tallestDown = grid[0][x]
        tallestUp = grid[height - 1][x]
        for (y in 1 until height) {
            val treeHeight = grid[y][x]
            if (treeHeight <= tallestDown) {
                shadow.increment(x, y)
            }
            tallestDown = max(tallestDown, treeHeight)

            val treeHeight2 = grid[height - 1 - y][x]
            if (treeHeight2 <= tallestUp) {
                shadow.increment(x, height - 1 - y)
            }
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
            if (treeHeight <= tallest) {
                shadow.increment(x, y)
            }
            tallest = max(tallest, treeHeight)
        }
        tallest = row.last()
        row.withIndex().reversed().drop(1).forEach { (x, treeHeight) ->
            if (treeHeight <= tallest) {
                shadow.increment(x, y)
            }
            tallest = max(tallest, treeHeight)
        }
    }
}

private fun List<MutableList<Int>>.increment(x: Int, y: Int) {
    this[y][x] = this[y][x] + 1
}

private fun Sequence<Int>.score(height: Int, max: Int): Int {
    if (none()) return 0
    val score = takeWhile { it < height }.count()
    return if (score == max) {
        score
    } else {
        score + 1
    }
}

private enum class Operation { Decrement, Increment}

private fun getAllInDirection(value: Int, direction: Operation, height: Int) =
    when (direction) {
        Operation.Decrement -> if (value == 0) emptySequence() else (value - 1 downTo 0).asSequence()
        Operation.Increment -> if (value == height - 1) emptySequence() else (value + 1 until height).asSequence()
    }
