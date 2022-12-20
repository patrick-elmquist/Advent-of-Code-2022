
import day.day
import util.sliceByBlank
import util.toInts

// answer #1: 71780
// answer #2: 212489

fun main() {
    day(n = 1) {
        part1(expected = 71780) { input ->
            input.lines.sliceByBlank()
                .maxOfOrNull { group -> group.toInts().sum() }
        }

        part2(expected = 212489) { input ->
            input.lines.sliceByBlank()
                .map { group -> group.toInts().sum() }
                .sortedDescending()
                .take(3)
                .sum()
        }
    }
}
