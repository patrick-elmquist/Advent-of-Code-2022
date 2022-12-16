
import day.Input
import day.day
import java.util.*

// answer #1: 1701
// answer #2: 2455

fun main() {
    day(n = 16) {
        part1(expected = 1701) { input ->
            val parsed = input.parse()
            val map = parsed.associateBy { it.name }
            val current = map.getValue("AA")

            val cache = parsed.flatMap { a -> a.edge.map { b -> key(a.name, b) to 1 } }
                .distinct()
                .toMap()
                .toMutableMap()

            map.values.forEach { a ->
                map.values.forEach { b ->
                    map.shortestPath(a, b, cache)
                }
            }

            map.solve(current, (map.values.toList() - current).filter { it.rate > 0 }, 30, cache)
        }
        part1 test 1 expect 1651

        part2(expected = 2455) { input ->
            val parsed = input.parse()
            val map = parsed.associateBy { it.name }
            val current = map.getValue("AA")

            val cache = parsed.flatMap { a -> a.edge.map { b -> key(a.name, b) to 1 } }
                .distinct()
                .toMap()
                .toMutableMap()

            map.values.forEach { a ->
                map.values.forEach { b ->
                    map.shortestPath(a, b, cache)
                }
            }

            val listOfLists = create((map.values.toList() - current).filter { it.rate > 0 })
            val cache2 = mutableMapOf<Int, Int>()
            listOfLists.maxOf { (me, elephant) ->
                val meKey = me.hashCode()
                val my = cache2[meKey] ?: map.solve(
                    current,
                    me,
                    26,
                    cache
                ).also { cache2[meKey] = it }

                val elephantKey = elephant.hashCode()
                val elephants = cache2[elephantKey] ?: map.solve(
                    current,
                    elephant,
                    26,
                    cache
                ).also { cache2[elephantKey] = it }

                my + elephants
            }
        }
        part2 test 1 expect 1707
    }
}

private fun create(l: List<Vertex>): List<Pair<List<Vertex>, List<Vertex>>> {
    val list = mutableListOf<Pair<List<Vertex>, List<Vertex>>>()
    val flags = BooleanArray(l.size)
    var i = 0

    fun inv(i: Int): Boolean {
        flags[i] = !flags[i]
        return flags[i]
    }

    while (i != l.size) {
        val a = mutableListOf<Vertex>()
        val b = mutableListOf<Vertex>()
        for (j in l.indices) if (flags[j]) a.add(l[j]) else b.add(l[j])
        list.add(a to b)
        i = 0

        while (i < l.size && !inv(i)) {
            i++
        }
    }
    return list
}

private fun Map<String, Vertex>.solve(
    current: Vertex,
    left: List<Vertex>,
    remaining: Int,
    cache: MutableMap<String, Int>
): Int {
    if (remaining <= 0) return 0

    var remainingTime = remaining
    var expectedPressure = 0
    if (current.rate > 0) {
        expectedPressure = (remainingTime - 1) * current.rate
        remainingTime -= 1
    }

    if (left.none { cache.getValue(key(it, current)) + 1 <= remainingTime }) {
        return expectedPressure
    }

    return expectedPressure + left
        .filter { cache.getValue(key(it, current)) + 1 <= remainingTime }
        .maxOf {
            solve(
                current = it,
                left = left - it,
                remaining = remainingTime - cache.getValue(key(it, current)),
                cache
            ).also { "back in ${current.name}" }
        }
}

private fun key(a: Vertex, b: Vertex): String = key(a.name, b.name)
private fun key(a: String, b: String): String = listOf(a, b).sorted().joinToString("")
private fun Map<String, Vertex>.shortestPath(a: Vertex, b: Vertex, cache: MutableMap<String, Int>): Int {
    val key = key(a, b)
    val cached = cache[key]
    if (cached != null) return cached

    val distances = mutableMapOf<String, Int>()
    distances.putAll(values.map { if (it == a) it.name to 0 else it.name to Int.MAX_VALUE })
    val queue = PriorityQueue<String> { a, b -> distances.getValue(a).compareTo(distances.getValue(b)) }
    queue.add(a.name)
    val prev = mutableListOf<String>()
    while (queue.isNotEmpty()) {
        val current = getValue(queue.poll())

        if (current == b) break

        current.edge.forEach {
            val alt = distances.getValue(current.name) + 1
            if (alt < distances.getValue(it)) {
                distances[it] = alt
                prev.add(it)
                queue.remove(it)
                queue.add(it)
            }
        }
    }

    val value = distances.getValue(b.name)
    return value.also { cache[key] = value }
}

private fun Input.parse(): List<Vertex> {
    return lines.map {
        val split = it.split(" ")
        val valve = split[1]
        val rate = split[4].removePrefix("rate=").removeSuffix(";").toInt()
        val connections = split.subList(9, split.size).map {
            it.removeSuffix(",")
        }
        Vertex(valve, rate, connections)
    }
}

private data class Vertex(val name: String, val rate: Int, val edge: List<String>)