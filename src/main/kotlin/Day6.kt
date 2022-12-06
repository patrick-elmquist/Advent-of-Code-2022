import day.day
import day.single

// answer #1: 1929
// answer #2: 3298

fun main() {
    day(n = 6) {
        fun String.findMarker(n: Int): Int =
            n + windowed(size = n, transform = CharSequence::toSet)
                .indexOfFirst { it.size == n }

        part1 { input ->
            input.single().findMarker(n = 4)
        }

        part2 { input ->
            input.single().findMarker(n = 14)
        }
    }
}
