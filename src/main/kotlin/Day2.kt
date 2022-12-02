
import day.day

// answer #1: 11906
// answer #2: 11186

const val rock = 1
const val paper = 2
const val scissor = 3
fun main() {
    day(n = 2) {
        part1(expected = 11906) { input ->
            input.lines.map { it.split(" ") }
                .map {  (first, second) ->
                    when (first.first()) {
                        'A' -> {
                            when (second.first()) {
                                'X' -> rock + 3
                                'Y' -> 6 + paper
                                'Z' -> scissor
                                else -> error("")
                            }
                        }
                        'B' -> {
                            when (second.first()) {
                                'X' -> rock
                                'Y' -> paper + 3
                                'Z' -> 6 + scissor
                                else -> error("")
                            }
                        }
                        'C' -> {
                            when (second.first()) {
                                'X' -> 6 + rock
                                'Y' -> paper
                                'Z' -> scissor + 3
                                else -> error("")
                            }
                        }
                        else -> error("")
                    }
                }.sum()
        }

        val test = """
            A Y
            B X
            C Z""".trimIndent()
        part1 verify test expect 15

        part2(expected = 11186) { input ->
            input.lines.map { it.split(" ") }
                .map {  (first, second) ->
                    when (first.first()) {
                        'A' -> {
                            when (second.first()) {
                                'X' -> scissor
                                'Y' -> rock + 3
                                'Z' -> paper + 6
                                else -> error("")
                            }
                        }
                        'B' -> {
                            when (second.first()) {
                                'X' -> rock
                                'Y' -> paper + 3
                                'Z' -> scissor + 6
                                else -> error("")
                            }
                        }
                        'C' -> {
                            when (second.first()) {
                                'X' -> paper
                                'Y' -> scissor + 3
                                'Z' -> rock + 6
                                else -> error("")
                            }
                        }
                        else -> error("")
                    }
                }.sum()
        }
        part2 verify test expect 12
    }
}
