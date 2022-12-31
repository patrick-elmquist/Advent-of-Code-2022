package day08

import common.day
import common.util.Matrix
import common.util.MutableMatrix
import kotlin.math.max

// answer #1: 1818
// answer #2: 368368

fun main() {
    day(n = 8) {
        part1(expected = 1818) { input ->
            val lines = input.lines
            val grid = lines.map { row -> row.map(Char::digitToInt) }
            val cover = lines.map { row -> row.map { 0 }.toMutableList() }

            cover.calculateVerticalCover(grid)
            cover.calculateHorizontalCover(grid)

            val totalTrees = grid.size * grid.size
            totalTrees - cover.sumOf { row -> row.count { it == 4 } }
        }
        part1 test 1 expect 21

        part2(expected = 368368) { input ->
            val grid = input.lines.map { row -> row.map(Char::digitToInt) }
            grid.flatMapIndexed { y, row ->
                row.mapIndexed { x, treeHeight ->
                    listOf(
                        indicesInDirection(y, Direction.Decrement, grid.size)
                            .map { y -> grid[y][x] }.score(treeHeight, y),
                        indicesInDirection(y, Direction.Increment, grid.size)
                            .map { y -> grid[y][x] }.score(treeHeight, grid.lastIndex - y),
                        indicesInDirection(x, Direction.Decrement, grid.size)
                            .map { x -> grid[y][x] }.score(treeHeight, x),
                        indicesInDirection(x, Direction.Increment, grid.size)
                            .map { x -> grid[y][x] }.score(treeHeight, grid.lastIndex - x)
                    ).reduce(Int::times)
                }
            }.max()
        }
        part2 test 1 expect 8
    }
}

private fun MutableMatrix<Int>.calculateVerticalCover(grid: Matrix<Int>) {
    for (x in grid.indices) {
        // checking from top
        (1 until grid.size).fold(grid[0][x]) { tallest, y ->
            val treeHeight = grid[y][x]
            if (treeHeight <= tallest) increment(x, y)
            max(tallest, treeHeight)
        }

        // checking from bottom
        (grid.lastIndex - 1 downTo 0).fold(grid[grid.lastIndex][x]) { tallest, y ->
            val treeHeight = grid[y][x]
            if (treeHeight <= tallest) increment(x, y)
            max(tallest, treeHeight)
        }
    }
}

private fun MutableMatrix<Int>.calculateHorizontalCover(grid: Matrix<Int>) {
    grid.forEachIndexed { y, row ->
        row.withIndex().drop(1)
            .fold(row.first()) { tallest, (x, treeHeight) ->
                if (treeHeight <= tallest) increment(x, y)
                max(tallest, treeHeight)
            }
        row.withIndex().reversed().drop(1)
            .fold(row.last()) { tallest, (x, treeHeight) ->
                if (treeHeight <= tallest) increment(x, y)
                max(tallest, treeHeight)
            }
    }
}

private fun MutableMatrix<Int>.increment(x: Int, y: Int) {
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

private enum class Direction { Decrement, Increment }

private fun indicesInDirection(value: Int, direction: Direction, height: Int) =
    when (direction) {
        Direction.Increment -> if (value == height - 1) emptySequence() else (value + 1 until height).asSequence()
        Direction.Decrement -> if (value == 0) emptySequence() else (value - 1 downTo 0).asSequence()
    }
