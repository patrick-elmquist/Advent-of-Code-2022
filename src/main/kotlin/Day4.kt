import day.day

// answer #1: 547
// answer #2: 843

fun main() {
    day(n = 4) {
        fun List<String>.toSets() =
            map { line ->
                line.split(',').map { pair ->
                    val (start, end) = pair.split("-")
                    (start.toInt()..end.toInt()).toSet()
                }
            }

        part1 { input ->
            input.lines
                .toSets()
                .count { (a, b) -> a.containsAll(b) || b.containsAll(a) }
        }

        part2 { input ->
            input.lines
                .toSets()
                .count { (a, b) -> a.intersect(b).isNotEmpty() }
        }
    }
}

