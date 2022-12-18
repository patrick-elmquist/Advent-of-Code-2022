import day.Input
import day.day
import util.Point3D
import util.neighbors

// answer #1: 4450
// answer #2: 2564

fun main() {
    day(n = 18) {
        part1(expected = 4450) { input ->
            val cubes = input.parseCubes()
            surfaceAreaOfCluster(cubes)
        }
        part1 test 1 expect 10
        part1 test 2 expect 22
        part1 test 3 expect 64
        part1 test 4 expect 108
        part1 test 5 expect 36

        part2(expected = 2564) { input ->
            val cubes = input.parseCubes()

            val minX = cubes.minOf { it.x }
            val maxX = cubes.maxOf { it.x }

            val minY = cubes.minOf { it.y }
            val maxY = cubes.maxOf { it.y }

            val minZ = cubes.minOf { it.z }
            val maxZ = cubes.maxOf { it.z }

            val inverse = mutableSetOf<Point3D>()

            var volume = 0
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    for (z in minZ..maxZ) {
                        volume++
                        inverse.add(Point3D(x, y, z).takeIf { it !in cubes } ?: continue)
                    }
                }
            }

            val queue = mutableListOf(inverse.first())
            val visited = mutableSetOf<Point3D>()
            val clusters = mutableSetOf<Set<Point3D>>()
            val cluster = mutableSetOf<Point3D>()
            while (queue.isNotEmpty() || visited.size < inverse.size) {
                val air = queue.removeLastOrNull() ?: inverse.first { it !in visited }.also {
                    clusters.add(cluster.toSet())
                    cluster.clear()
                }

                if (air in visited) continue

                cluster.add(air)
                visited += air

                air.neighbors().filter { it in inverse }.forEach { n ->
                    queue.add(n)
                }
            }
            clusters.add(cluster.toSet())
            val clustersWithin = clusters.filter { cluster ->
                cluster.none {
                    it.x == minX || it.x == maxX || it.y == minY || it.y == maxY || it.z == minZ || it.z == maxZ
                }
            }

            val surfaceAreaWithin = clustersWithin
                .sumOf { f -> surfaceAreaOfCluster(f) }

            val allSurfaceArea = surfaceAreaOfCluster(cubes)

            allSurfaceArea - surfaceAreaWithin
        }
        part2 test 3 expect 58
    }
}

private fun surfaceAreaOfCluster(cubes: Collection<Point3D>): Int {
    val usedSides = cubes.sumOf { cube -> cube.neighbors().count { it in cubes } }
    return cubes.size * 6 - usedSides
}

private fun Input.parseCubes() =
    lines.map {
        val (x, y, z) = it.split(",").map(String::toInt)
        Point3D(x, y, z)
    }
