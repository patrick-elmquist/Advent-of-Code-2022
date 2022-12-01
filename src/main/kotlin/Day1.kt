import day.day
import util.splitOnBlank
import util.toInts

// answer #1: 71780
// answer #2: 212489

fun main() {
    day(n = 1) {
        part1(expected = 71780) { input ->
            input.lines
                .splitOnBlank()
                .maxOfOrNull { line -> line.toInts().sum() }
        }

        part2(expected = 212489) { input ->
            input.lines
                .splitOnBlank()
                .map { line -> line.toInts().sum() }
                .sortedDescending()
                .take(3)
                .sum()
        }
    }
}
