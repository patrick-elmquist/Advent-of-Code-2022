
import day.day
import day.single

// answer #1: 1929
// answer #2: 3298

fun main() {
    day(n = 6) {
        part1 { input ->
            input.single()
                .windowed(4, 1) { it.toSet() }
                .withIndex()
                .first { it.value.size == 4 }
                .index + 4

        }
        part1 test 1 expect 7
        part1 test 2 expect 5
        part1 test 3 expect 6

        part2 { input ->
            input.single().windowed(14, 1) { it.toSet() }
            .withIndex()
            .first { it.value.size == 14 }
            .index + 14
        }
        part2 test 1 expect 19
        part2 test 2 expect 23
        part2 test 3 expect 23
    }
}

