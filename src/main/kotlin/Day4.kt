import day.day

// answer #1: 547
// answer #2: 843

fun main() {
    day(n = 4) {
        fun List<String>.toNumberSets() =
            map { line ->
                line.split(',').map { pair ->
                    val (start, end) = pair.split("-")
                    (start.toInt()..end.toInt()).toSet()
                }
            }

        part1(expected = 547) { input ->
            input.lines
                .toNumberSets()
                .count { (a, b) -> a.containsAll(b) || b.containsAll(a) }
        }

        part2(expected = 843) { input ->
            input.lines
                .toNumberSets()
                .count { (a, b) -> a.intersect(b).isNotEmpty() }
        }
    }
}

