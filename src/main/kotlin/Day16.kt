
import day.Input
import day.day
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
            val cache = mutableMapOf<Int, Int>()
            workDivision.maxOf { (me, elephant) ->
                val meKey = me.hashCode()
                val my = cache[meKey] ?: solve(
                    valve = start,
                    closedValves = me,
                    timeRemaining = 26,
                    distances = distances
                ).also { cache[meKey] = it }

                val elephantKey = elephant.hashCode()
                val elephants = cache[elephantKey] ?: solve(
                    valve = start,
                    closedValves = elephant,
                    timeRemaining = 26,
                    distances = distances
                ).also { cache[elephantKey] = it }

                my + elephants
            }
        }
        part2 test 1 expect 1707
    }
}

private fun Input.parseNameValveMap(): Map<String, Valve> {
    val parsed = lines.map {
        val split = it.split(" ")
        val valve = split[1]
        val rate = split[4].removePrefix("rate=").removeSuffix(";").toInt()
        val connections = split.subList(9, split.size).map { it.removeSuffix(",") }
        Valve(valve, rate, connections)
    }
    return parsed.associateBy { it.name }
}

private fun findAllDistances(allValves: Map<String, Valve>): Map<Int, Int> {
    val distances = mutableMapOf<Int, Int>()
    allValves.values.forEach { start ->
        allValves.values.forEach { end ->
            val key = cacheKey(start, end)
            if (distances[key] == null) {
                distances[key] = shortestPath(allValves, start, end)
            }
        }
    }
    return distances
}

private fun shortestPath(allValves: Map<String, Valve>, start: Valve, end: Valve): Int {
    val distances = allValves.values
        .associate { it.name to if (it == start) 0 else Int.MAX_VALUE }
        .toMutableMap()

    val comparator: (String, String) -> Int = { a, b ->
        distances.getValue(a).compareTo(distances.getValue(b))
    }
    val queue = PriorityQueue(comparator).apply { add(start.name) }

    while (queue.isNotEmpty()) {
        val current = allValves.getValue(queue.poll())

        if (current == end) break

        current.connections.forEach { valve ->
            val newDistance = distances.getValue(current.name) + 1
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

    fun inv(i: Int): Boolean {
        flags[i] = !flags[i]
        return flags[i]
    }

    while (i != l.size) {
        val a = mutableListOf<Valve>()
        val b = mutableListOf<Valve>()

        for (j in l.indices) if (flags[j]) a.add(l[j]) else b.add(l[j])

        list.add(listOf(a, b))

        i = 0
        while (i < l.size && !inv(i)) i++
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
    if (valve.rate > 0) {
        totalPressure = (timeAfter - 1) * valve.rate
        timeAfter -= 1
    }

    if (closedValves.none { distances.getValue(cacheKey(it, valve)) + 1 <= timeAfter }) {
        return totalPressure
    }

    return totalPressure + closedValves
        .filter { distances.getValue(cacheKey(it, valve)) + 1 <= timeAfter }
        .maxOf {
            solve(
                valve = it,
                closedValves = closedValves - it,
                timeRemaining = timeAfter - distances.getValue(cacheKey(it, valve)),
                distances
            )
        }
}

private fun cacheKey(a: Valve, b: Valve): Int = cacheKey(a.name, b.name)

private fun cacheKey(a: String, b: String): Int = listOf(a, b).sorted().hashCode()

private data class Valve(val name: String, val rate: Int, val connections: List<String>)
