import day.day
import util.sliceByBlank

// answer #1: 57348
// answer #2:

fun main() {
    day(n = 11) {
        part1(expected = 57348) { input ->
            val monkeys = input.lines.parse()
            val map = monkeys.associateBy { it.id }
            var round = 0
            val counter = monkeys.map { it.id to 0 }.toMap().toMutableMap()
            while (round < 20) {
                monkeys.forEach { monkey ->
                    counter[monkey.id] = counter.getValue(monkey.id) + monkey.items.size
                    monkey.items.forEach { level ->
                        val (op, value) = monkey.operation.split(" ")
                        val newLevel = if (value == "old") {
                            calculateWorryLevel(op, level, level)
                        } else {
                            calculateWorryLevel(op, level, value.toLong())
                        } / 3
                        val nextMonkey = if (newLevel % monkey.divisibleBy == 0L) {
                            monkey.monkeyOnTrue
                        } else {
                            monkey.monkeyOnFalse
                        }
                        map.getValue(nextMonkey).items.add(newLevel)
                    }
                    monkey.items.clear()
                }
                round++
            }
            counter.values.sortedDescending().take(2).reduce(Int::times)
        }
        part1 test 1 expect 10605

        part2(expected = 14106266886L) { input ->
            val monkeys = input.lines.parse()
            val map = monkeys.associateBy { it.id }
            var round = 0
            val rounds = 10000
            val counter = monkeys.map { it.id to 0L }.toMap().toMutableMap()
            val lcm = monkeys.map { it.divisibleBy }.reduce(Long::times)
            while (round < rounds) {
                monkeys.forEach { monkey ->
                    counter[monkey.id] = counter.getValue(monkey.id) + monkey.items.size
                    monkey.items.forEach { level ->
                        val (op, value) = monkey.operation.split(" ")
                        val newLevel = if (value == "old") {
                            calculateWorryLevel(op, level, level)
                        } else {
                            calculateWorryLevel(op, level, value.toLong())
                        } % lcm
                        if (newLevel % monkey.divisibleBy == 0L) {
                            map.getValue(monkey.monkeyOnTrue).items.add(newLevel)
                        } else {
                            map.getValue(monkey.monkeyOnFalse).items.add(newLevel)
                        }
                    }
                    monkey.items.clear()
                }
                round++
            }
            counter.values.sortedDescending().take(2).reduce(Long::times)

        }
        part2 test 1 expect 2713310158L
    }
}

private fun calculateWorryLevel(op: String, a: Long, b: Long): Long {
    return when (op) {
        "+" -> a + b
        "*" -> a * b
        else -> error("")
    }
}

data class Monkey(
    val id: Int,
    val items: MutableList<Long> = mutableListOf(),
    val operation: String,
    val divisibleBy: Long,
    val monkeyOnTrue: Int,
    val monkeyOnFalse: Int
)

private fun List<String>.parse(): List<Monkey> {
    return sliceByBlank()
        .mapIndexed { index, lines ->
            Monkey(
                id = index,
                items = lines[1].trim().removePrefix("Starting items:").trim()
                    .split(", ")
                    .map(String::toLong)
                    .toMutableList(),
                operation = lines[2].trim().removePrefix("Operation: new = old "),
                divisibleBy = lines[3].split(" ").last().toLong(),
                monkeyOnTrue = lines[4].split(" ").last().toInt(),
                monkeyOnFalse = lines[5].split(" ").last().toInt()
            )
        }
}