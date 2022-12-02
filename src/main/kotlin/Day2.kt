import day.Input
import day.day

// answer #1: 11906
// answer #2: 11186

fun main() {
    day(n = 2) {
        part1(expected = 11906) { input ->
            input.parse().sumOf { (opponent, player) ->
                score(opponent, player)
            }
        }

        part2(expected = 11186) { input ->
            input.parse().sumOf { (opponent, outcome) ->
                score(opponent, counterMoveFromOutcome(opponent, outcome))
            }
        }
    }
}

private fun Input.parse() = lines.map { it[0] - 'A' + 1 to it[2] - 'X' + 1 }

private fun counterMoveFromOutcome(opponent: Int, outcome: Int): Int {
    return 1 + (opponent + outcome).mod(3)
}

private fun score(opponent: Int, player: Int): Int {
    return (1 + player - opponent).mod(3) * 3 + player
}