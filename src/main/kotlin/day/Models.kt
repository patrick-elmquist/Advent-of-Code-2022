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

typealias Algorithm = ((Input) -> Any?)
class Sheet {
    private val tests = mutableListOf<Test>()

    private val _parts = mutableListOf<Part>()
    val parts: List<Part>
        get() = _parts.toList()

    var breakAdded: Boolean = false
    var ignore: Boolean = false

    val part1 get() = parts.first { it.number == 1 }.algorithm

    val part2 get() = parts.first { it.number == 2 }.algorithm

    fun verify(expected: Any?, actual: Any?) {
        check(expected == actual) {
            println("Expected: $expected but got: $actual")
        }
    }

    infix fun Algorithm.verify(input: Input): Any? =
        invoke(input)

    infix fun Algorithm.verify(string: String): Any? =
        invoke(Input(string))

    infix fun Algorithm.verify(lines: List<String>): Any? =
        invoke(Input(lines))

    infix fun Any?.expect(expected: Any?) =
        verify(expected, this)

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
