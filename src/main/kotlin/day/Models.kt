package day

import kotlin.time.Duration

enum class PartNumber { One, Two }

data class Part(
    val type: PartNumber,
    val algorithm: (Input) -> Any?,
    val expected: Any?,
    val testOnly: Boolean
) {
    val number get() = when (type) {
        PartNumber.One -> 1
        PartNumber.Two -> 2
    }
}

data class Test(
    val part: PartNumber,
    val input: Input,
    val expected: Any?
)

data class Answer(
    val number: Int,
    val output: Any?,
    val time: Duration
)

typealias Algorithm = ((Input) -> Any?)

class Sheet(private val day: Int) {
    private val _parts = mutableListOf<Part>()
    val parts: List<Part>
        get() = _parts.toList()

    private val _tests = mutableListOf<Test>()
    val tests: List<Test>
        get() = _tests.toList()

    var breakAdded: Boolean = false
    var ignore: Boolean = false

    val part1 get() = TestBuilder(PartNumber.One)

    val part2 get() = TestBuilder(PartNumber.Two)

    fun part1(expected: Any? = null, block: (Input) -> Any?) =
        addPart(PartNumber.One, expected, block)

    fun part2(expected: Any? = null, block: (Input) -> Any?) =
        addPart(PartNumber.Two, expected, block)

    @Suppress("unused")
    fun stop() {
        breakAdded = true
    }

    @Suppress("unused")
    fun ignore() {
        ignore = true
    }

    class TestBuilder(val part: PartNumber) {
        var input: Input? = null
    }

    infix fun TestBuilder.test(test: Int): TestBuilder {
        input = Input(day = day, test = test)
        return this
    }

    infix fun TestBuilder.test(string: String): TestBuilder {
        input = Input(string)
        return this
    }

    infix fun TestBuilder.test(lines: List<String>): TestBuilder {
        input = Input(lines)
        return this
    }

    infix fun TestBuilder.expect(expected: Any?) {
        if (!breakAdded) {
            _tests += Test(part, requireNotNull(input), expected)
        }
    }

    private fun addPart(n: PartNumber, expected: Any?, block: (Input) -> Any?) {
        check(_parts.none { it.type == n })
        if (!ignore) {
            _parts += Part(n, block, expected, breakAdded)
        }
        ignore = breakAdded
    }
}
