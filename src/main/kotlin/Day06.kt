import day.day

// answer #1: 1929
// answer #2: 3298

fun main() {
    day(n = 6) {
        fun String.findMarker(n: Int): Int =
            n + windowed(size = n, transform = CharSequence::toSet)
                .indexOfFirst { it.size == n }

        part1(expected = 1929) { input ->
            input.lines.single().findMarker(n = 4)
        }

        part2(expected = 3298) { input ->
            input.lines.single().findMarker(n = 14)
        }
    }
}
