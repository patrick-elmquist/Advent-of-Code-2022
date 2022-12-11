import day.day
import util.sliceByBlank

// answer #1: 57348
// answer #2: 14106266886

fun main() {
    day(n = 11) {
        part1(expected = 57348L) { input ->
            val monkeys = input.lines.parseMonkeys()
            monkeys.calculateMonkeyBusiness(
                rounds = 20,
                applyRelief = { it / 3 }
            )
        }
        part1 test 1 expect 10605L

        part2(expected = 14106266886L) { input ->
            val monkeys = input.lines.parseMonkeys()
            val lcm = monkeys.map { it.divisibleBy }.reduce(Long::times)
            monkeys.calculateMonkeyBusiness(
                rounds = 10_000,
                applyRelief = { it % lcm }
            )
        }
        part2 test 1 expect 2713310158L
    }
}

private fun Array<Monkey>.calculateMonkeyBusiness(rounds: Int, applyRelief: (Long) -> Long): Long {
    val throwCounter = Array(size) { 0 }
    repeat(rounds) {
        forEach { monkey ->
            throwCounter[monkey.id] += monkey.throwItems(
                monkeys = this,
                applyRelief = applyRelief
            )
        }
    }
    return throwCounter.sortedDescending()
        .take(2)
        .map(Int::toLong)
        .reduce(Long::times)
}

private fun Monkey.throwItems(monkeys: Array<Monkey>, applyRelief: (Long) -> Long): Int =
    with(items) {
        forEach { level ->
            val resolvedValue = if (operationValue == "old") level else operationValue.toLong()
            val worryLevel = when (operation) {
                "+" -> applyRelief(level + resolvedValue)
                else -> applyRelief(level * resolvedValue)
            }
            val nextMonkey = if (worryLevel % divisibleBy == 0L) nextOnTrue else nextOnFalse
            monkeys[nextMonkey].items += worryLevel
        }
        size.also { clear() }
    }

data class Monkey(
    val id: Int,
    val items: MutableList<Long> = mutableListOf(),
    val operation: String,
    val operationValue: String,
    val divisibleBy: Long,
    val nextOnTrue: Int,
    val nextOnFalse: Int
)

private fun List<String>.parseMonkeys(): Array<Monkey> =
    map(String::trim)
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
                nextOnTrue = lines[4].substringAfterLast(" ").toInt(),
                nextOnFalse = lines[5].substringAfterLast(" ").toInt()
            )
        }
        .toTypedArray()

