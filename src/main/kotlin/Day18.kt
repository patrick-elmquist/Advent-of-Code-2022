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
        part1 test 2 expect 64

        part2(expected = 2564) { input ->
            val cubes = input.parseCubes()

            // contain the droplet in a box of air
            val minX = cubes.minOf { it.x }
            val maxX = cubes.maxOf { it.x }

            val minY = cubes.minOf { it.y }
            val maxY = cubes.maxOf { it.y }

            val minZ = cubes.minOf { it.z }
            val maxZ = cubes.maxOf { it.z }

            val cubesOfAir = mutableSetOf<Point3D>()
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    for (z in minZ..maxZ) {
                        cubesOfAir.add(Point3D(x, y, z).takeIf { it !in cubes } ?: continue)
                    }
                }
            }

            val queue = ArrayDeque<Point3D>().apply { add(cubesOfAir.first()) }
            val visited = mutableSetOf<Point3D>()
            val clustersOfAir = mutableSetOf<Set<Point3D>>()
            val currentCluster = mutableSetOf<Point3D>()
            while (queue.isNotEmpty() || visited.size < cubesOfAir.size) {
                val cubeOfAir = queue.removeFirstOrNull()
                    ?: cubesOfAir.first { it !in visited }.also {
                        clustersOfAir.add(currentCluster.toSet())
                        currentCluster.clear()
                    }

                if (cubeOfAir in visited) continue

                currentCluster.add(cubeOfAir)
                visited += cubeOfAir

                cubeOfAir.neighbors()
                    .filter { it in cubesOfAir && it !in visited }
                    .forEach { n -> queue.add(n) }
            }
            clustersOfAir.add(currentCluster.toSet())

            val surfaceAreaWithinDroplet = clustersOfAir
                .filter { cluster ->
                    // If there's no connection to the containing box, it's within the droplet
                    cluster.none { (x, y, z) ->
                        x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ
                    }
                }
                .sumOf { f -> surfaceAreaOfCluster(f) }

            surfaceAreaOfCluster(cubes) - surfaceAreaWithinDroplet
        }
        part2 test 2 expect 58
    }
}

private fun Input.parseCubes() =
    lines.map { line ->
        line.split(",").map(String::toInt).let { (x, y, z) -> Point3D(x, y, z) }
    }

private fun surfaceAreaOfCluster(cluster: Collection<Point3D>): Int =
    cluster.sumOf { cube -> 6 - cube.neighbors().count { it in cluster } }
