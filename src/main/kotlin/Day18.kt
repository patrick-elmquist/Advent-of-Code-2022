import day.day
import util.Point3D
import util.neighbors

// answer #1: 4450
// answer #2:

fun main() {
    day(n = 18) {
        part1(expected = 4450) { input ->
            val cubes = input.lines.map {
                val (x, y, z) = it.split(",").map(String::toInt)
                Point3D(x, y, z)
            }.toSet()
            val consumed = cubes.sumOf { it.neighbors().count { it in cubes } }
            (cubes.size * 6 - consumed)
        }
        part1 test 1 expect 10
        part1 test 2 expect 22
        part1 test 3 expect 64
        part1 test 4 expect 108
        part1 test 5 expect 36

        part2 { input ->

        }
        part2 test 1 expect Unit
    }
}