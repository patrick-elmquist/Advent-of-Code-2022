import day.day

// answer #1:
// answer #2:

fun main() {
    day(n = 3) {
        part1 { input ->
            input.lines.map {
                val (first, second) = it.chunked(it.length / 2).map { it.toSet() }
                println(first)
                println(second)
                val union = first.intersect(second).single()
                if (union.isUpperCase()) {
                    union - 'A' + 1 + 26
                } else {
                    union - 'a' + 1
                }
            }
                .sum()
        }

        val testInput = """
            vJrwpWtwJgWrhcsFMMfFFhFp
            jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
            PmmdzqPrVvPwwTWBwg
            wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
            ttgJtRGJQctTZtZT
            CrZsJsPPZsGzwwsLwLmpwMDw""".trimIndent()

        part1 verify testInput expect 157
        part2 { input ->
            input.lines.chunked(3) {
                val (first, second, third) = it.map { it.toSet() }
                val union = first.intersect(second).intersect(third).single()
                if (union.isUpperCase()) {
                    union - 'A' + 1 + 26
                } else {
                    union - 'a' + 1
                }
            }.sum()
        }
        part2 verify testInput expect 70
    }
}
