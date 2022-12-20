import day.day
import day.toInts
import util.log
import kotlin.math.abs

// answer #1: not 6766, too low
// answer #2:

fun main() {
    day(n = 20) {
        part1 { input ->
            val og = input.lines

            val ints = input.toInts()
            val first = og.first().toInt()
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
        }
//        part1 test 2 expect 10
        part1 test 1 expect 3

        part2 { input ->

        }
        part2 test 1 expect Unit
    }
}

private fun solve2(head: Node, idsInOrder: List<String>, idNodeMap: Map<String, Node>): Int {
    print(head)
    idsInOrder.forEach { id ->
        val node = idNodeMap.getValue(id)
        when {
            node.value < 0 -> {
                var prev = node.back
                val cBack = node.back
                val cHead = node.forward
                cHead.prev = cBack
                cBack.next = cHead
                repeat(abs(node.value)) {
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
                val cBack = node.back
                val cHead = node.forward
                cHead.prev = cBack
                cBack.next = cHead
                repeat(abs(node.value) - 1) {
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

    print(head)
    var index1 = Int.MIN_VALUE
    var index2 = Int.MIN_VALUE
    var index3 = Int.MIN_VALUE
    var i = 1
    var current = idNodeMap.values.first { it.value == 0 }.log("node 0")
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
    return index1.log("1000th:") + index2.log("2000th:") + index3.log("3000th:")
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
    val value: Int,
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