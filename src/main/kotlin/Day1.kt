import day.day

fun main() {
    day(n = 1) {

        part1 {
            it.lines.forEach { println(it) }
            "Tist e"

        }
        val testInput = """
            this
            is
            multiple
            lines
        """.trimIndent()

        part1 verify testInput expect 3

        part2 {

        }
    }
}
