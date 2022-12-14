package day04

import common.day

// answer #1: 547
// answer #2: 843

fun main() {
    day(n = 4) {
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

private fun List<String>.toNumberSets() =
    map { line ->
        line.split(',').map { pair ->
            val (start, end) = pair.split("-")
            (start.toInt()..end.toInt()).toSet()
        }
    }

