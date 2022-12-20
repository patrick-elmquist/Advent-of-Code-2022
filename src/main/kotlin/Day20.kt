import day.day
import kotlin.math.abs

// answer #1: 7395
// answer #2: 1640221678213

fun main() {
    day(n = 20) {
        part1(expected = 7395L) { input ->
            val idNodeMap = idToNodeMap(input.lines.map(String::toLong))
            val idsInOrder = idNodeMap.keys.sorted()

            performMixing(idsInOrder, idNodeMap)

            findResult(idNodeMap)
        }
        part1 test 1 expect 3L

        part2(expected = 1640221678213L) { input ->
            val multipliedInput = input.lines.map(String::toLong).map { it * 811589153L }
            val idNodeMap = idToNodeMap(multipliedInput)
            val idsInOrder = idNodeMap.keys.sorted()

            repeat(times = 10) { performMixing(idsInOrder, idNodeMap) }

            findResult(idNodeMap)
        }
        part2 test 1 expect 1623178306L
    }
}

private fun idToNodeMap(longs: List<Long>): Map<Int, Node> {
    var prev: Node? = null
    val idNodeMap = longs.mapIndexed { index, i ->
        index to Node(i).also {
            prev?.next = it
            it.prev = prev
            prev = it
        }
    }.toMap()
    val head = idNodeMap.getValue(0)
    prev?.next = head
    head.prev = prev
    return idNodeMap
}

private fun findResult(idNodeMap: Map<Int, Node>): Long =
    forwardSequence(idNodeMap.values.first { it.value == 0L })
        .take(3)
        .map(Node::value)
        .reduce(Long::plus)

private fun forwardSequence(head: Node) =
    generateSequence(head) { node ->
        var current = node
        repeat(1000) { current = current.forward }
        current
    }.drop(1)

private fun performMixing(idsInOrder: List<Int>, idNodeMap: Map<Int, Node>) {
    val adjustedSize = idsInOrder.size - 1
    idsInOrder.forEach { id ->
        val node = idNodeMap.getValue(id)
        if (node.value == 0L) return@forEach
        var prev = if (node.value < 0) node.back else node.forward
        node.forward.prev = node.back
        node.back.next = node.forward
        val absoluteMovement = abs(node.value)
        when {
            node.value < 0 -> repeat((absoluteMovement % adjustedSize).toInt()) {
                prev = prev.back
            }

            else -> repeat(((absoluteMovement - 1) % adjustedSize).toInt()) {
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
