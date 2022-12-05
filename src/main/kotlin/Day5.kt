import day.day
import util.groupByBlank

// answer #1: PTWLTDSJV
// answer #2: WZMFVGGZP

fun main() {
    day(n = 5) {
        part1(expected = "PTWLTDSJV") { input ->
            val (top, bottom) = input.lines.groupByBlank()
            val (stacks, instructions) = top.toStacks() to bottom.toInstructions()
            stacks.rearrange(instructions, reverseOrder = true)
        }

        part2(expected = "WZMFVGGZP") { input ->
            val (top, bottom) = input.lines.groupByBlank()
            val (stacks, instructions) = top.toStacks() to bottom.toInstructions()
            stacks.rearrange(instructions, reverseOrder = false)
        }
    }
}

private fun Stacks.rearrange(
    instructions: List<Instruction>,
    reverseOrder: Boolean
): String {
    instructions.forEach { (count, from, to) ->
        val end = get(from).size
        val items = get(from).subList(end - count, end).toMutableList()
        repeat(count) {
            get(from).removeLast()
        }
        if (reverseOrder) items.reverse()
        get(to).addAll(items)
    }
    return map { it.last() }.joinToString("")
}

private fun List<String>.toStacks(): Stacks {
    val stackCount = last().trim().split("\\s+".toRegex()).count()
    return dropLast(1)
        .reversed()
        .fold(MutableList(stackCount) { mutableListOf() }) { stack, line ->
            stack.apply {
                line.withIndex()
                    .drop(1)
                    .windowed(size = 1, step = 4) { it.first() }
                    .filter { it.value != ' ' }
                    .forEach { (index, char) -> get(index / 4).add(char) }
            }
        }
}

private fun List<String>.toInstructions(): List<Instruction> {
    val pattern = """move (\d+) from (\d+) to (\d+)""".toRegex()
    return map { instruction ->
        val (c, f, t) = pattern.matchEntire(instruction)?.destructured
            ?: error("regex failed to match content for $instruction")
        Instruction(c.toInt(), f.toInt() - 1, t.toInt() - 1)
    }
}

private typealias Stacks = List<MutableList<Char>>

private data class Instruction(val count: Int, val from: Int, val to: Int)
