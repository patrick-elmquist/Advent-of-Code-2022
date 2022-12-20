
import day.day
import util.sliceByBlank

// answer #1: 71780
// answer #2: 212489

fun main() {
    day(n = 1) {
        part1(expected = 71780) { input ->
            input.lines.sliceByBlank()
                .maxOfOrNull { group -> group.map(String::toInt).sum() }
        }

        part2(expected = 212489) { input ->
            input.lines.sliceByBlank()
                .map { group -> group.map(String::toInt).sum() }
                .sortedDescending()
                .take(3)
                .sum()
        }
    }
}
