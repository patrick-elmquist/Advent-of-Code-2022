package common.util

fun main() {
    // isBitSet tests
    0b101.isBitSet(0) assert true
    0b101.isBitSet(1) assert false
    0b101.isBitSet(2) assert true

    println("Test OK")
}

fun Int.isBitSet(index: Int): Boolean = (this shr index) and 1 != 0

fun Int.getBit(index: Int): Int = if (isBitSet(index)) 1 else 0

fun Collection<Int>.minToMax(): IntRange = minOf { it }..maxOf { it }

private infix fun <T> T.assert(expected: T) = assert(this == expected) { "Assert failed: $this != $expected" }
