import day.day

// answer #1: 17180
// answer #2:
// ###  #### #  # ###  ###  #    #  # ###
// #  # #    #  # #  # #  # #    #  # #  #
// #  # ###  #### #  # #  # #    #  # ###
// ###  #    #  # ###  ###  #    #  # #  #
// # #  #    #  # #    # #  #    #  # #  #
// #  # #### #  # #    #  # ####  ##  ###

fun main() {
    day(n = 10) {
        part1(expected = 17180) { input ->
            var signalSum = 0

            input.lines.toRegisterIncrements()
                .forEachCycle { cycle, x ->
                    if (cycle % 40 == 20) {
                        signalSum += cycle * x
                    }
                }

            signalSum
        }

        part2 { input ->
            val image = Array(6) { Array(40) { '.' } }

            input.lines.toRegisterIncrements()
                .forEachCycle { cycle, x -> image.draw(cycle, x) }

            image.print()
        }
    }
}

private fun List<String>.toRegisterIncrements(): List<Int> =
    flatMap {
        if (it.startsWith("noop")) {
            listOf(0)
        } else {
            listOf(0, it.substringAfterLast(" ").toInt())
        }
    }

private fun List<Int>.forEachCycle(onCycle: (cycle: Int, x: Int) -> Unit) =
    withIndex().fold(1) { x, (cycle, increment) ->
        onCycle(cycle + 1, x)
        x + increment
    }

private fun Array<Array<Char>>.draw(cycles: Int, x: Int) {
    val width = first().size
    val pixel = cycles - 1
    val i = pixel / width
    val j = pixel % width
    this[i][j] = if (j in (x - 1..x + 1)) '#' else ' '
}

private fun Array<Array<Char>>.print() =
    forEach { row ->
        row.forEach { pixel -> print(pixel) }
        println()
    }