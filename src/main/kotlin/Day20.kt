import day.day
import kotlin.math.abs

// answer #1: 7395
// answer #2: 1640221678213

fun main() {
    day(n = 20) {
        part1(expected = 7395L) { input ->
            val longs = input.lines.map(String::toLong)
            val idNodeMap = idToNodeMap(longs)
            val idsInOrder = idNodeMap.keys.map(String::toInt).sorted().map(Int::toString)
            solve(idsInOrder, idNodeMap)
            findResult(idNodeMap)
        }
        part1 test 1 expect 3L

        part2(expected = 1640221678213L) { input ->
            val longs = input.lines.map(String::toLong).map { it * 811589153L }
            val idNodeMap = idToNodeMap(longs)
            val idsInOrder = idNodeMap.keys.map(String::toInt).sorted().map(Int::toString)

            repeat(10) {
                solve(idsInOrder, idNodeMap)
            }
            findResult(idNodeMap)
        }
        part2 test 1 expect 1623178306L
    }
}

private fun idToNodeMap(longs: List<Long>): MutableMap<String, Node> {
    val first = longs.first()
    val head = Node(first)
    var prev = head
    val idNodeMap = longs.drop(1).mapIndexed { index, i ->
        val id = "${index + 1}"
        id to Node(i).also {
            prev.next = it
            it.prev = prev
            prev = it
        }
    }.toMap().toMutableMap()
    idNodeMap["0"] = head
    prev.next = head
    head.prev = prev
    return idNodeMap
}

private fun findResult(idNodeMap: MutableMap<String, Node>): Long =
    buildList {
        var current = idNodeMap.values.first { it.value == 0L }
        for (i in 1..3000) {
            current = current.forward
            if (i == 1000 || i == 2000 || i == 3000) {
                add(current.value)
            }
        }
    }.sum()

private fun solve(idsInOrder: List<String>, idNodeMap: Map<String, Node>) {
    val adjustedSize = idsInOrder.size - 1
    idsInOrder.forEach { id ->
        val node = idNodeMap.getValue(id)
        if (node.value == 0L) return@forEach
        var prev = if (node.value < 0) node.back else node.forward
        node.forward.prev = node.back
        node.back.next = node.forward
        val absoluteMovementd = abs(node.value)
        when {
            node.value < 0 -> repeat((absoluteMovementd % adjustedSize).toInt()) {
                prev = prev.back
                if (prev == node) {
                    prev = prev.back
                }
            }

            else -> repeat(((absoluteMovementd - 1) % adjustedSize).toInt()) {
                prev = prev.forward
            }
        }
        val nHead = prev.forward
        prev.next = node
        node.prev = prev
        nHead.prev = node
        node.next = nHead
    }
}

private class Node(
    val value: Long,
    var prev: Node? = null,
    var next: Node? = null
) {
    val forward get() = requireNotNull(next)
    val back get() = requireNotNull(prev)
}
