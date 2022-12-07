import day.Input
import day.day
import util.sliceByBlank

// answer #1: PTWLTDSJV
// answer #2: WZMFVGGZP

private const val INPUT_OFFSET = 4

fun main() {
    day(n = 5) {
        part1(expected = "PTWLTDSJV") { input ->
            input.parseInstructions()
                .fold(input.parseStacks()) { stacks, instruction ->
                    stacks.move(instruction, reverseOrder = true)
                }
                .output()
        }

        part2(expected = "WZMFVGGZP") { input ->
            input.parseInstructions()
                .fold(input.parseStacks()) { stacks, instruction ->
                    stacks.move(instruction, reverseOrder = false)
                }
                .output()
        }
    }
}

private data class Instruction(val count: Int, val from: Int, val to: Int)

private data class Stacks(val stacks: List<List<Char>>) {
    fun move(instruction: Instruction, reverseOrder: Boolean): Stacks {
        val (count, indexFrom, indexTo) = instruction
        val from = stacks[indexFrom]
        val to = stacks[indexTo]

        val itemsToMove = if (reverseOrder) {
            from.takeLast(count).reversed()
        } else {
            from.takeLast(count)
        }

        return Stacks(
            stacks.toMutableList().apply {
                set(indexFrom, from.dropLast(count))
                set(indexTo, to + itemsToMove)
            }
        )
    }

    fun output() = stacks.map { it.last() }.joinToString("")
}

private fun Input.parseInstructions(): List<Instruction> =
    lines.sliceByBlank().let { (_, bottom) ->
        val pattern = """move (\d+) from (\d+) to (\d+)""".toRegex()
        bottom.map { instruction ->
            val (c, f, t) = pattern.matchEntire(instruction)?.destructured
                ?: error("regex failed to match content for $instruction")
            Instruction(c.toInt(), f.toInt() - 1, t.toInt() - 1)
        }
    }

private fun Input.parseStacks(): Stacks =
    lines.sliceByBlank().let { (top, _) ->
        val stackCount = top.last().trim().split("\\s+".toRegex()).count()
        val initialStacks = List(stackCount) { mutableListOf<Char>() }
        val indices = generateIndices(stackCount)
        top.dropLast(1)
            .reversed()
            .flatMap { line -> indices.map { index -> index to line.getOrNull(index) } }
            .filter { (_, c) -> c != null && c != ' ' }
            .forEach { (i, c) -> initialStacks[i / INPUT_OFFSET].add(c!!) }
        return Stacks(initialStacks)
    }

private fun generateIndices(count: Int): List<Int> {
    return generateSequence(1) { it + INPUT_OFFSET }
        .take(count)
        .toList()
}
