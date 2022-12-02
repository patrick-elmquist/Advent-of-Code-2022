import day.day

// answer #1: 11906
// answer #2: 11186

fun main() {
    day(n = 2) {
        part1(expected = 11906) { input ->
            input.lines.sumOf { line ->
                val moveValue = line[2].code - 'W'.code
                moveValue + when (line[0] to line[2]) {
                    'A' to 'Y',
                    'B' to 'Z',
                    'C' to 'X' -> 6

                    'A' to 'X',
                    'B' to 'Y',
                    'C' to 'Z' -> 3

                    else -> 0
                }
            }
        }

        part2(expected = 11186) { input ->
            input.lines.sumOf { line ->
                val (opponent, outcome) = line[0] to line[2]
                when (outcome) {
                    'X' -> when (opponent) {
                        'A' -> 3
                        'B' -> 1
                        else -> 2
                    }

                    'Y' -> 3 + opponent.code - '@'.code

                    else -> 6 + when (opponent) {
                        'A' -> 2
                        'B' -> 3
                        else -> 1
                    }
                }
            }
        }
    }
}
