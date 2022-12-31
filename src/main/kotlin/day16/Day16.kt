package day16

import common.Input
import common.day
import java.util.*

// answer #1: 1701
// answer #2: 2455

fun main() {
    day(n = 16) {
        part1(expected = 1701) { input ->
            val nameToValveMap = input.parseNameValveMap()
            val start = nameToValveMap.getValue("AA")
            val closedValeWithValidRate = (nameToValveMap.values.toList() - start)
                .filter { it.rate > 0 }
            solve(
                valve = start,
                closedValves = closedValeWithValidRate,
                timeRemaining = 30,
                distances = findAllDistances(nameToValveMap)
            )
        }
        part1 test 1 expect 1651

        part2(expected = 2455) { input ->
            val nameToValveMap = input.parseNameValveMap()
            val start = nameToValveMap.getValue("AA")

            val closedValeWithValidRate = (nameToValveMap.values.toList() - start)
                .filter { it.rate > 0 }

            val workDivision = allPossibleWorkDivision(closedValeWithValidRate)
            val distances = findAllDistances(nameToValveMap)
            val localCache = mutableMapOf<Int, Int>()
            workDivision.maxOf { (myWork, elephantsWork) ->
                val meKey = myWork.hashCode()
                val myPressure = localCache[meKey] ?: solve(
                    valve = start,
                    closedValves = myWork,
                    timeRemaining = 26,
                    distances = distances
                ).also { localCache[meKey] = it }

                val elephantKey = elephantsWork.hashCode()
                val elephantsPressure = localCache[elephantKey] ?: solve(
                    valve = start,
                    closedValves = elephantsWork,
                    timeRemaining = 26,
                    distances = distances
                ).also { localCache[elephantKey] = it }

                myPressure + elephantsPressure
            }
        }
        part2 test 1 expect 1707
    }
}

private fun Input.parseNameValveMap(): Map<String, Valve> =
    lines.map {
        val split = it.split(" ")
        val valve = split[1]
        val rate = split[4].removePrefix("rate=").removeSuffix(";").toInt()
        val connections = split.subList(9, split.size).map { it.removeSuffix(",") }
        Valve(valve, rate, connections)
    }.associateBy { it.name }

private fun findAllDistances(allValves: Map<String, Valve>): Map<Int, Int> =
    buildMap {
        allValves.values.forEach { start ->
            allValves.values.forEach { end ->
                putIfAbsent(cacheKey(start, end), shortestPath(allValves, start, end))
            }
        }
    }

private fun shortestPath(allValves: Map<String, Valve>, start: Valve, end: Valve): Int {
    val distances = allValves.mapValues {  (_, valve) -> if (valve == start) 0 else Int.MAX_VALUE }
        .toMutableMap()

    val comparator: (String, String) -> Int = { a, b ->
        distances.getValue(a).compareTo(distances.getValue(b))
    }
    val queue = PriorityQueue(comparator).apply { add(start.name) }

    while (queue.isNotEmpty()) {
        val valve = allValves.getValue(queue.poll())

        if (valve == end) break

        val currentDistance = distances.getValue(valve.name)
        valve.connections.forEach { valve ->
            val newDistance = currentDistance + 1
            if (newDistance < distances.getValue(valve)) {
                distances[valve] = newDistance
                // wasteful way of refreshing the queue
                queue.remove(valve)
                queue.add(valve)
            }
        }
    }

    return distances.getValue(end.name)
}

private fun allPossibleWorkDivision(l: List<Valve>): List<List<List<Valve>>> {
    val list = mutableListOf<List<List<Valve>>>()
    val flags = BooleanArray(l.size)
    var i = 0

    fun invFlag(i: Int): Boolean {
        flags[i] = !flags[i]
        return flags[i]
    }

    while (i != l.size) {
        val a = mutableListOf<Valve>()
        val b = mutableListOf<Valve>()

        for (j in l.indices) if (flags[j]) a.add(l[j]) else b.add(l[j])

        list.add(listOf(a, b))

        i = 0
        while (i < l.size && !invFlag(i)) i++
    }
    return list
}

private fun solve(
    valve: Valve,
    closedValves: List<Valve>,
    timeRemaining: Int,
    distances: Map<Int, Int>
): Int {
    if (timeRemaining <= 0) return 0

    var timeAfter = timeRemaining
    var totalPressure = 0
    if (valve.rate > 0) { // only needed for AA
        timeAfter -= 1
        totalPressure = timeAfter * valve.rate
    }

    if (closedValves.none { distances.getValue(cacheKey(it, valve)) + 1 < timeAfter }) {
        return totalPressure
    }

    return totalPressure + closedValves
        .filter { distances.getValue(cacheKey(it, valve)) + 1 < timeAfter }
        .maxOf {
            solve(
                valve = it,
                closedValves = closedValves - it,
                timeRemaining = timeAfter - distances.getValue(cacheKey(it, valve)),
                distances = distances
            )
        }
}

private fun cacheKey(a: Valve, b: Valve): Int = cacheKey(a.name, b.name)

private fun cacheKey(a: String, b: String): Int = listOf(a, b).sorted().hashCode()

private data class Valve(val name: String, val rate: Int, val connections: List<String>)
