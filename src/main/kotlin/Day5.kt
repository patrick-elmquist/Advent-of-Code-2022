
import common.log
import day.day
import util.groupByBlank

// answer #1: PTWLTDSJV
// answer #2:

fun main() {
    day(n = 5) {
        part1(expected = "PTWLTDSJV") {
            val (top, instructions) = it.lines.groupByBlank()

            val cols = top.last().trim().split("\\s+".toRegex()).count()
            val list = List<MutableList<Char>>(cols) { mutableListOf() }

            top.dropLast(1)
                .forEach { line ->
                    line.withIndex().filter {
                        it.index in listOf(1, 5, 9, 13, 17, 21, 25, 29, 33, 37)
                    }.map { it.value }.withIndex().filter { it.value != ' ' }.onEach{ it.log() }
                        .forEach {
                            list.get((it.index).coerceAtLeast(0)).add(it.value)
                        }
                }

            list.log()
            instructions.forEach {
                val split = it.split(" ")
                val count = split[1].toInt()
                val from = split[3].toInt() - 1
                val to = split[5].toInt() - 1
                repeat(count) {
                    val item = list.get(from).removeFirst()
                    list.get(to).add(0, item)
                }
            }
            list.log()
            list.map { it.first() }.joinToString("")
        }

        val testInput = """
            |    [D]    
            |[N] [C]    
            |[Z] [M] [P]
            | 1   2   3 

            |move 1 from 2 to 1
            |move 3 from 1 to 3
            |move 2 from 2 to 1
            |move 1 from 1 to 2 """.trimMargin("|")
        part1 verify testInput expect "CMZ"
        part2 {

        }
    }
}
