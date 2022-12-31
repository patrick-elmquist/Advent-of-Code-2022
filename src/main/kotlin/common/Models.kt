package common

import kotlin.time.Duration

enum class PartId(val n: Int) { One(1), Two(2) }

data class Part(
    val partId: PartId,
    val algorithm: (Input) -> Any?,
    val expected: Any?
) {
    val number get() = partId.n
}

data class Test(
    val partId: PartId,
    val input: Input,
    val expected: Any?
)

data class Answer(
    val output: Any?,
    val time: Duration
)

@Suppress("unused", "MemberVisibilityCanBePrivate")
class Sheet(val day: Int) {
    private val _parts = mutableListOf<Part>()
    val parts: List<Part>
        get() = _parts.toList()

    private val _tests = mutableListOf<Test>()
    val tests: List<Test>
        get() = _tests.toList()

    var breakAfterTest: Boolean = false
        private set

    var ignorePart1 = false
    val part1 get() = TestBuilder(PartId.One)

    var ignorePart2 = false
    val part2 get() = TestBuilder(PartId.Two)

    fun part1(expected: Any? = null, block: (Input) -> Any?) =
        addPart(PartId.One, expected, block)

    fun part2(expected: Any? = null, block: (Input) -> Any?) =
        addPart(PartId.Two, expected, block)

    fun breakAfterTest() {
        breakAfterTest = true
    }

    infix fun TestBuilder.test(test: Int): TestBuilder =
        apply { input = Input(day = day, test = test) }

    infix fun TestBuilder.test(lines: List<String>): TestBuilder =
        apply { input = Input(lines) }

    infix fun TestBuilder.expect(expected: Any?) {
        _tests += Test(part, requireNotNull(input), expected)
    }

    private fun addPart(n: PartId, expected: Any?, block: (Input) -> Any?) {
        check(_parts.none { it.partId == n })
        if (ignorePart1 && n == PartId.One) return
        if (ignorePart2 && n == PartId.Two) return
        _parts += Part(n, block, expected)
    }

    class TestBuilder(val part: PartId) {
        var input: Input? = null
    }
}
