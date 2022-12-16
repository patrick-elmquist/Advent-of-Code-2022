import day.Input
import day.day
import util.log
import java.util.*

// answer #1: 1701
// answer #2: not 2654, too high

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

            map.solve(current, (map.values.toList() - current).filter { it.rate > 0 }, 30, cache).log()
        }
        part1 test 1 expect 1651

        part2 { input ->
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

//            val me = Player("me", current, 26)
//            val elephant = Player("elephant", current, 26)

            val listOfLists = create((map.values.toList() - current).filter { it.rate > 0 })
            var cache2 = mutableMapOf<Int, Int>()
            listOfLists.maxOf { (me, elephant) ->
//                "cache: $cache2".log()
//                "me: $me".log()
//                "elephant: $elephant".log()
//                "".log()
//                Thread.sleep(250L)

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
        val a: ArrayList<Vertex> = ArrayList()
        val b: ArrayList<Vertex> = ArrayList()
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
    if (remaining <= 0) {
        return 0
    }

    var remaining = remaining
    var expectedPressure = 0
    if (current.rate > 0) {
        expectedPressure = (remaining - 1) * current.rate
        remaining -= 1
    }

    if (left.none { cache.getValue(key(it, current)) + 1 <= remaining }) {
        return expectedPressure
    }

    return expectedPressure + left
        .filter { cache.getValue(key(it, current)) + 1 <= remaining }
        .maxOf {
            solve(
                current = it,
                left = left - it,
                remaining = remaining - cache.getValue(key(it, current)),
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