package day03

import common.day

// answer #1: 7763
// answer #2: 2569

fun main() {
    day(n = 3) {
        part1(expected = 7763) { input ->
            input.lines.sumOf { line ->
                val (left, right) = line.chunked(line.length / 2).map(String::toSet)
                left.intersect(right).single().priority
            }
        }
        part2(expected = 2569) { input ->
            input.lines.chunked(3) { group ->
                val (first, second, third) = group.map(String::toSet)
                first.intersect(second).intersect(third).single().priority
            }.sum()
        }
    }
}

private val Char.priority: Int
    get() = 1 + if (isUpperCase()) this - 'A' + 26 else this - 'a'
