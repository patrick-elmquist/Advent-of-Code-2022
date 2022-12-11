import day.Input
import day.day
import util.sliceByBlank

// answer #1: 57348
// answer #2: 14106266886

fun main() {
    day(n = 11) {
        part1(expected = 57348L) { input ->
            input.parseMonkeys().solve(rounds = 20, mapLevel = { it / 3 })
        }
        part1 test 1 expect 10605L

        part2(expected = 14106266886L) { input ->
            val monkeys = input.parseMonkeys()
            val lcm = monkeys.map { it.divisibleBy }.reduce(Long::times)
            input.parseMonkeys().solve(rounds = 10_000, mapLevel = { it % lcm })
        }
        part2 test 1 expect 2713310158L
    }
}

private fun Input.parseMonkeys(): List<Monkey> = lines
    .map(String::trim)
    .sliceByBlank()
    .mapIndexed { index, lines ->
        Monkey(
            id = index,
            items = lines[1].removePrefix("Starting items: ")
                .split(", ")
                .map(String::toLong)
                .toMutableList(),
            operation = lines[2].removePrefix("Operation: new = old "),
            divisibleBy = lines[3].substringAfterLast(" ").toLong(),
            monkeyOnTrue = lines[4].substringAfterLast(" ").toInt(),
            monkeyOnFalse = lines[5].substringAfterLast(" ").toInt()
        )
    }

private fun List<Monkey>.solve(rounds: Int, mapLevel: (Long) -> Long): Long {
    val monkeys = this
    val counter = monkeys.associate { it.id to 0L }.toMutableMap()
    repeat(rounds) {
        monkeys.forEach { monkey ->
            counter[monkey.id] = counter.getValue(monkey.id) + monkey.items.size
            monkey.items.forEach { level ->
                val (op, value) = monkey.operation.split(" ")
                var newLevel = if (value == "old") {
                    calculateWorryLevel(op, level, level)
                } else {
                    calculateWorryLevel(op, level, value.toLong())
                }
                newLevel = mapLevel(newLevel)
                val nextMonkey = if (newLevel % monkey.divisibleBy == 0L) {
                    monkey.monkeyOnTrue
                } else {
                    monkey.monkeyOnFalse
                }
                monkeys[nextMonkey].items += newLevel
            }
            monkey.items.clear()
        }
    }
    return counter.values.sortedDescending().take(2).reduce(Long::times)
}

private fun calculateWorryLevel(op: String, a: Long, b: Long): Long =
    when (op) {
        "+" -> a + b
        "*" -> a * b
        else -> error("")
    }

data class Monkey(
    val id: Int,
    val items: MutableList<Long> = mutableListOf(),
    val operation: String,
    val divisibleBy: Long,
    val monkeyOnTrue: Int,
    val monkeyOnFalse: Int
)
