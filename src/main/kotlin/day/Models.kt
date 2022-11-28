package day

import kotlin.time.Duration

data class Part(
    val number: Int,
    val algorithm: (Input) -> Any?,
    val expected: Any?,
    val tests: List<Test>,
    val testOnly: Boolean
)

data class Test(
    val input: String,
    val expected: Any?
)

data class Answer(
    val number: Int,
    val output: Any?,
    val time: Duration
)

class Sheet {
    private val tests = mutableListOf<Test>()

    private val _parts = mutableListOf<Part>()
    val parts: List<Part>
        get() = _parts.toList()

    var breakAdded: Boolean = false
    var ignore: Boolean = false

    fun part1(expected: Any? = null, block: (Input) -> Any?) = addPart(1, expected, block)

    fun part2(expected: Any? = null, block: (Input) -> Any?) = addPart(2, expected, block)

    @Suppress("unused")
    fun stop() {
        breakAdded = true
    }

    @Suppress("unused")
    fun ignore() {
        ignore = true
    }

    @Suppress("unused")
    infix fun String.expect(expected: Any?) {
        if (!breakAdded) {
            tests += Test(this, expected)
        }
    }

    private fun addPart(n: Int, expected: Any?, block: (Input) -> Any?) {
        check(_parts.none { it.number == n })
        if (!ignore) {
            _parts += Part(n, block, expected, tests.toList(), breakAdded)
        }
        ignore = breakAdded
        tests.clear()
    }
}
