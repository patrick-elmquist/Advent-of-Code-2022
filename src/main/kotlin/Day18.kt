import day.day
import util.Point3D
import util.log
import util.neighbors

// answer #1: 4450
// answer #2: 2564

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

        part2(expected = 2564) { input ->
            val cubes = input.lines.map {
                val (x, y, z) = it.split(",").map(String::toInt)
                Point3D(x, y, z)
            }.toSet()

            val minX = cubes.minOf { it.x }.log("minx")
            val maxX = cubes.maxOf { it.x }.log("maxx")

            val minY = cubes.minOf { it.y }.log("miny")
            val maxY = cubes.maxOf { it.y }.log("maxy")

            val minZ = cubes.minOf { it.z }.log("minz")
            val maxZ = cubes.maxOf { it.z }.log("maxz")

            val inverse = mutableSetOf<Point3D>()

            var volume = 0
            for (x in minX .. maxX) {
                for (y in minY .. maxY) {
                    for (z in minZ .. maxZ) {
                        volume++
                        val p = Point3D(x, y, z)
                        if (p !in cubes) {
                            inverse.add(p)
                        }
                    }
                }
            }

            volume.log("volume")
            cubes.size.log("cubes size")
//            inverse.log("inv")
            inverse.size.log("inv size")

            val queue = mutableListOf(inverse.first())
            val visited = mutableSetOf<Point3D>()
            val clusters = mutableSetOf<Set<Point3D>>()
            val cluster = mutableSetOf<Point3D>()
            while (queue.isNotEmpty() || visited.size < inverse.size) {
                val air = queue.removeLastOrNull() ?: inverse.first { it !in visited }
                    .also {
                        clusters.add(cluster.toSet())
                        cluster.clear()
                    }

                if (air in visited) continue

                cluster.add(air)
                visited += air

                air.neighbors()
                    .filter { it in inverse }
                    .forEach { n ->
                        queue.add(n)
                    }
            }
            clusters.add(cluster.toSet())
//            clusters.log("clusters")
            clusters.size.log("clusters count")
            clusters.forEach { it.size.log("cluster count") }

            val filtered = clusters.filter {
                it.none {
                    it.x == minX || it.x == maxX ||
                            it.y == minY || it.y == maxY ||
                            it.z == minZ || it.z == maxZ
                }
            }

            val s = filtered.sumOf { f ->
                val consumedP1 = f.sumOf { it.neighbors().count { it in f } }
                (f.size * 6 - consumedP1)
            }

            val consumedP1 = cubes.sumOf { it.neighbors().count { it in cubes } }
            val r = (cubes.size * 6 - consumedP1)

            val internal = inverse.filter { inv -> filtered.none { inv in it } }
            val consumed = internal.sumOf { it.neighbors().count { it in cubes } }
            r - s
        }
        part2 test 3 expect 58
    }
}