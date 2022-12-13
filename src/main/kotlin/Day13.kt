import day.day
import util.sliceByBlank

// answer #1: 5717
// answer #2: 25935

fun main() {
    day(n = 13) {
        part1(expected = 5717) { input ->
            input.lines.sliceByBlank()
                .map { it.map(::parseToPacket) }
                .mapIndexedNotNull { index, (left, right) -> index.takeIf { left < right } }
                .sumOf { it + 1 }
        }
        part1 test 1 expect 13

        part2(expected = 25935) { input ->
            val packets = input.lines.filter { it.isNotBlank() }.map(::parseToPacket)
            val dividerTwo = Nested(Value(2))
            val dividerSix = Nested(Value(6))
            val sortedPackets = (packets + dividerTwo + dividerSix).sorted()
            (sortedPackets.indexOf(dividerTwo) + 1) * (sortedPackets.indexOf(dividerSix) + 1)

        }
        part2 test 1 expect 140
    }
}

private fun parseToPacket(input: String): Packet {
    val unwrapped = input.removeSurrounding("[", "]")

    if (unwrapped.isEmpty()) {
        return Nested(emptyList())
    }

    if (unwrapped.toIntOrNull() != null) {
        return Value(unwrapped.toInt())
    }

    var line = unwrapped
    val packets = mutableListOf<Packet>()
    while (line.isNotEmpty()) {
        line = when {
            line.startsWith(',') -> {
                line.drop(1)
            }

            line.startsWith("[") -> {
                val nextBlock = getNextBlock(line)
                packets.add(parseToPacket(nextBlock))
                line.drop(nextBlock.length)
            }

            else -> {
                val value = line.substringBefore(',')
                packets.add(Value(value.toInt()))
                line.drop(value.length)
            }
        }
    }
    return Nested(packets)
}

private fun getNextBlock(remaining: String): String {
    var level = 0
    remaining.forEachIndexed { index, c ->
        when (c) {
            '[' -> level++
            ']' -> level--
        }
        if (level == 0) return remaining.substring(0, index + 1)
    }
    error("failed to find block $remaining")
}

private sealed class Packet : Comparable<Packet> {
    override fun compareTo(other: Packet): Int = when {
        this is Value && other is Value -> {
            value.compareTo(other.value)
        }

        this is Nested && other is Nested -> {
            content.zip(other.content).forEach { (l, r) ->
                val compare = l.compareTo(r)
                if (compare != 0) return compare
            }
            content.size.compareTo(other.content.size)
        }

        this is Value && other is Nested -> {
            Nested(this).compareTo(other)
        }

        this is Nested && other is Value -> {
            this.compareTo(Nested(other))
        }

        else -> {
            error("left:$this right:$other")
        }
    }
}

private data class Value(val value: Int) : Packet()
private data class Nested(val content: List<Packet>) : Packet() {
    constructor(vararg packets: Packet) : this(packets.toList())
}