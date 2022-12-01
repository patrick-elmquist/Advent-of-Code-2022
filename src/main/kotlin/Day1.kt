
import day.day
import day.groupByBlank
import util.toInts

// answer #1: 71780
// answer #2: 212489

fun main() {
    day(n = 1) {
        part1(expected = 71780) { input ->
            input.groupByBlank()
                .maxOfOrNull { group -> group.toInts().sum() }
        }

        part2(expected = 212489) { input ->
            input.groupByBlank()
                .map { group -> group.toInts().sum() }
                .sortedDescending()
                .take(3)
                .sum()
        }
    }
}
