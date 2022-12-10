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
            instructions.forEach { instruction ->
                when (instruction.first()) {
                    "noop" -> {
                        cycles++
                        if (cycles % divider == 0) {
                            points.add(cycles * x)
                            divider += 40
                        }
                    }
                    else -> {
                        cycles ++
                        if (cycles % divider == 0) {
                            points.add(cycles * x)
                            divider += 40
                        }
                        cycles++
                        x += instruction.last().toInt()
                        if (cycles % divider == 0) {
                            points.add(cycles * x)
                            divider += 40
                        }
                    }
                }
            }

            points.sum()
        }

        part2 { input ->
            val instructions = input.lines.map { it.split(" ") }

            var cycles = 0
            var x = 1
            val image = Array(6) { Array(40) { "@" } }
            instructions.forEach { instruction ->
                cycles++
                image.draw(cycles, x)
                when (instruction.first()) {
                    "noop" -> {
                    }
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

typealias Image = Array<Array<String>>

private fun Image.draw(pixel: Int, x: Int) {
    val cycles = pixel - 1
    val width = 40
    val image = this
    if ((cycles % width) in (x - 1..x + 1)) {
        val i = cycles / width
        val j = cycles % width
        image[i][j] = "#"
    } else {
        val i = cycles / width
        val j = cycles % width
        image[i][j] = "."
    }
}
private fun Image.print() {
    forEach { line ->
        line.forEach {  item ->
            print(item)
        }
        println()
    }
    println()
}