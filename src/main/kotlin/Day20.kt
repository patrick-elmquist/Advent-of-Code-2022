import day.day
import day.toInts
import day.toLongs
import util.log
import kotlin.math.abs

// answer #1: 7395
// answer #2:

fun main() {
    day(n = 20) {
        part1(expected = 7395L) { input ->
            val og = input.lines

            val ints = input.toLongs()
            val first = og.first().toLong()
            val headId = "0 $first"
            val head = Node(
                id = headId,
                value = first,
            )
            var prev = head
            val idsInOrder = ints.mapIndexed { index, i ->
                "$index $i"
            }
            val idNodeMap = ints.drop(1).mapIndexed { index, i ->
                val id = "${index + 1} $i"
                id to Node(id = id, value = i).also {
                    prev.next = it
                    it.prev = prev
                    prev = it
                }
            }.toMap().toMutableMap()
            idNodeMap[headId] = head
            prev.next = head
            head.prev = prev

            solve2(head, idsInOrder, idNodeMap)
            var index1 = Long.MIN_VALUE
            var index2 = Long.MIN_VALUE
            var index3 = Long.MIN_VALUE
            var i = 1
            var current = idNodeMap.values.first { it.value == 0L }.log("node 0")
            while (i <= 3001) {
                current = current.forward
                if (i == 1000) {
                    index1 = current.value
                }
                if (i == 2000) {
                    index2 = current.value
                }
                if (i == 3000) {
                    index3 = current.value
                }
                i++
            }
            index1.log("1000th:") + index2.log("2000th:") + index3.log("3000th:")
        }
//        part1 test 2 expect 10
        part1 test 1 expect 3L

        part2 { input ->
            val og = input.lines
            val key = 811589153L
            val ints = input.toLongs().map {
                it * key
            }
            val first = ints.first()
            val headId = "0 $first"
            val head = Node(
                id = headId,
                value = first,
            )
            var prev = head
            val idsInOrder = ints.mapIndexed { index, i ->
                "$index $i"
            }
            val idNodeMap = ints.drop(1).mapIndexed { index, i ->
                val id = "${index + 1} $i"
                id to Node(id = id, value = i).also {
                    prev.next = it
                    it.prev = prev
                    prev = it
                }
            }.toMap().toMutableMap()
            idNodeMap[headId] = head
            prev.next = head
            head.prev = prev

            print(head)
            repeat(10) {
                solve2(head, idsInOrder, idNodeMap)
            }
            var index1 = Long.MIN_VALUE
            var index2 = Long.MIN_VALUE
            var index3 = Long.MIN_VALUE
            var i = 1
            var current = idNodeMap.values.first { it.value == 0L }.log("node 0")
            while (i <= 3001) {
                current = current.forward
                if (i == 1000) {
                    index1 = current.value
                }
                if (i == 2000) {
                    index2 = current.value
                }
                if (i == 3000) {
                    index3 = current.value
                }
                i++
            }
            index1.log("1000th:") + index2.log("2000th:") + index3.log("3000th:")
        }
        part2 test 1 expect 1623178306L
    }
}

private fun solve2(head: Node, idsInOrder: List<String>, idNodeMap: Map<String, Node>) {
    idsInOrder.forEach { id ->
        val node = idNodeMap.getValue(id)
        when {
            node.value < 0 -> {
                var prev = node.back
                node.forward.prev = node.back
                node.back.next = node.forward
                repeat((abs(node.value) % (idsInOrder.size - 1)).toInt()) {
                    prev = prev.back
                    if (prev == node) {
                        prev = prev.back
                    }
                }
                val nHead = prev.forward
                prev.next = node
                node.prev = prev
                nHead.prev = node
                node.next = nHead
            }

            node.value > 0 -> {
                var prev = node.forward
                node.forward.prev = node.back
                node.back.next = node.forward
                repeat(((abs(node.value) - 1) % (idsInOrder.size - 1)).toInt()) {
                    prev = prev.forward
                }
                val nHead = prev.forward
                prev.next = node
                node.prev = prev
                nHead.prev = node
                node.next = nHead
            }

            else -> return@forEach
        }
//        print(head)
//        println()
    }

    print(idNodeMap.values.first { it.value == 0L })
}

private fun print(head: Node) {
    var current = head
    var start = true
    while (start || current != head) {
        print(current.value)
        print(", ")
        current = current.next!!
        start = false
    }
    println()
}

private class Node(
    val id: String,
    val value: Long,
    var prev: Node? = null,
    var next: Node? = null
) {
    val forward get() = next!!
    val back get() = prev!!
    override fun toString(): String {
        return buildString {
            append("Node(value:$value prev:${prev!!.value} next:${next!!.value})")
        }
    }
}