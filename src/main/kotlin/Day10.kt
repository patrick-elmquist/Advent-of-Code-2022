import day.day

// answer #1: 17180
// answer #2:
// ###..####.#..#.###..###..#....#..#.###..
// #..#.#....#..#.#..#.#..#.#....#..#.#..#.
// #..#.###..####.#..#.#..#.#....#..#.###..
// ###..#....#..#.###..###..#....#..#.#..#.
// #.#..#....#..#.#....#.#..#....#..#.#..#.
// #..#.####.#..#.#....#..#.####..##..###..

fun main() {
    day(n = 10) {
        part1(expected = 17180) { input ->
            val instructions = input.lines.map { it.split(" ") }

            var cycles = 1
            var x = 1
            var divider = 20
            val points = mutableListOf<Int>()

            fun addPointIfNeeded() {
                if (cycles % divider == 0) {
                    points.add(cycles * x)
                    divider += 40
                }
            }
            instructions.forEach { instruction ->
                when (instruction.first()) {
                    "noop" -> {
                        cycles++
                        addPointIfNeeded()
                    }
                    else -> {
                        cycles ++
                        addPointIfNeeded()
                        cycles++
                        x += instruction.last().toInt()
                        addPointIfNeeded()
                    }
                }
            }

            points.sum()
        }

        part2 { input ->
            val instructions = input.lines.map { it.split(" ") }

            var cycles = 0
            var x = 1
            val image = Array(6) { Array(40) { '.' } }
            instructions.forEach { instruction ->
                cycles++
                image.draw(cycles, x)
                when (instruction.first()) {
                    "noop" -> Unit
                    else -> {
                        cycles++
                        image.draw(cycles, x)
                        x += instruction.last().toInt()
                    }
                }
            }
            image.print()
        }
    }
}

private typealias Image = Array<Array<Char>>

private fun Image.draw(cycles: Int, x: Int) {
    val pixel = cycles - 1
    val width = first().size
    val i = pixel / width
    val j = pixel % width
    this[i][j] = if (j in (x - 1..x + 1)) '#' else '.'
}

private fun Image.print() {
    forEach { line ->
        line.forEach {  pixel -> print(pixel) }
        println()
    }
}