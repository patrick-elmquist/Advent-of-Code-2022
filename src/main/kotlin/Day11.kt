import day.Input
import day.day
import util.sliceByBlank

// answer #1: 57348
// answer #2: 14106266886

fun main() {
    day(n = 11) {
        part1(expected = 57348L) { input ->
            input.parseMonkeys().resolveThrows(
                rounds = 20,
                adjustWorryLevel = { it / 3 }
            )
        }
        part1 test 1 expect 10605L

        part2(expected = 14106266886L) { input ->
            val monkeys = input.parseMonkeys()
            val lcm = monkeys.map { it.divisibleBy }.reduce(Long::times)
            monkeys.resolveThrows(
                rounds = 10_000,
                adjustWorryLevel = { it % lcm }
            )
        }
        part2 test 1 expect 2713310158L
    }
}

private fun Input.parseMonkeys(): Array<Monkey> = lines
    .map(String::trim)
    .sliceByBlank()
    .mapIndexed { index, lines ->
        Monkey(
            id = index,
            items = lines[1].removePrefix("Starting items: ")
                .split(", ")
                .map(String::toLong)
                .toMutableList(),
            operation = lines[2].removePrefix("Operation: new = old ").split(" ").first(),
            operationValue = lines[2].removePrefix("Operation: new = old ").split(" ").last(),
            divisibleBy = lines[3].substringAfterLast(" ").toLong(),
            monkeyOnTrue = lines[4].substringAfterLast(" ").toInt(),
            monkeyOnFalse = lines[5].substringAfterLast(" ").toInt()
        )
    }
    .toTypedArray()

private fun Array<Monkey>.resolveThrows(rounds: Int, adjustWorryLevel: (Long) -> Long): Long {
    val counter = Array(size) { 0L }
    repeat(rounds) {
        forEach { monkey ->
            counter[monkey.id] += monkey.items.size.toLong()
            monkey.items.forEach { level ->
                var newLevel = if (monkey.operationValue == "old") {
                    calculateWorryLevel(monkey.operation, level, level)
                } else {
                    calculateWorryLevel(monkey.operation, level, monkey.operationValue.toLong())
                }
                newLevel = adjustWorryLevel(newLevel)
                val nextMonkey = if (newLevel % monkey.divisibleBy == 0L) {
                    monkey.monkeyOnTrue
                } else {
                    monkey.monkeyOnFalse
                }
                get(nextMonkey).items += newLevel
            }
            monkey.items.clear()
        }
    }
    return counter.sortedDescending().take(2).reduce(Long::times)
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
    val operationValue: String,
    val divisibleBy: Long,
    val monkeyOnTrue: Int,
    val monkeyOnFalse: Int
)
