import day.Input
import day.day
import util.groupByBlank

// answer #1: PTWLTDSJV
// answer #2: WZMFVGGZP

fun main() {
    day(n = 5) {
        part1(expected = "PTWLTDSJV") { input ->
            val (stacks, instructions) = input.parse()
            instructions
                .fold(stacks) { output, (count, from ,to) ->
                    output.apply {
                        repeat(count) {
                            get(to).add(get(from).removeLast())
                        }
                    }
                }.output()
        }

        part2(expected = "WZMFVGGZP") { input ->
            val (stacks, instructions) = input.parse()
            instructions
                .fold(stacks) { output, (count, from, to) ->
                    output.apply {
                        val stackFrom = get(from)
                        val end = stackFrom.size
                        val items = stackFrom.subList(end - count, end).toList()
                        repeat(count) {
                            stackFrom.removeLast()
                        }
                        get(to).addAll(items)
                    }
                }.output()
        }
    }
}

private fun Input.parse(): Pair<Stacks, List<Instruction>> {
    val (stacksInput, instructionsInput) = lines.groupByBlank()
    return stacksInput.toStacks() to instructionsInput.toInstructions()
}

private fun List<String>.toStacks(): Stacks {
    val cols = last().trim().split("\\s+".toRegex()).count()
    return dropLast(1)
        .reversed()
        .fold(MutableList(cols) { mutableListOf() }) { stack, line ->
            var index = 1
            while (index < line.length) {
                if (line[index] != ' ') {
                    stack[index / 4].add(line[index])
                }
                index += 4
            }
            stack
        }
}

private fun List<String>.toInstructions(): List<Instruction> {
    val pattern = """move (\d+) from (\d+) to (\d+)""".toRegex()
    return map { instruction ->
        val (c, f, t) = pattern.matchEntire(instruction)?.destructured!!
        Instruction(c.toInt(), f.toInt() - 1, t.toInt() - 1)
    }
}

private typealias Stacks = List<MutableList<Char>>

private fun Stacks.output() = map { it.last() }.joinToString("")
private data class Instruction(val count: Int, val from: Int, val to: Int)