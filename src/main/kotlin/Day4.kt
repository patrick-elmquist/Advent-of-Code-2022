import day.day

// answer #1: 547
// answer #2: 843

fun main() {
    day(n = 4) {
        part1 { input ->
            input.lines
                .toIntRanges()
                .count { (a, b) -> a in b || b in a }
        }

        part2 { input ->
            input.lines
                .toIntRanges()
                .count { (a, b) -> a.intersect(b).isNotEmpty() }
        }
    }
}

private fun List<String>.toIntRanges() = map { line ->
    line.split(',').map {
        val (start, end) = it.split("-")
        start.toInt()..end.toInt()
    }
}

private operator fun IntRange.contains(other: IntRange) =
    other.first in this && other.last in this
