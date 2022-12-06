import day.day
import day.single

// answer #1: 1929
// answer #2: 3298

fun main() {
    day(n = 6) {
        part1 { input ->
            input.single().findMarker(n = 4)
        }

        part2 { input ->
            input.single().findMarker(n = 14)
        }
    }
}

private fun String.findMarker(n: Int): Int =
    windowed(n, 1) { it.toSet() }
        .withIndex()
        .first { it.value.size == n }
        .index + n