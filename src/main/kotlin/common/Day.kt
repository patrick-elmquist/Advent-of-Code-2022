@file:Suppress("NOTHING_TO_INLINE")

package common

import kotlinx.coroutines.runBlocking
import kotlin.time.measureTimedValue

fun day(
    n: Int,
    block: Sheet.() -> Unit
) = runBlocking {
    if (makeSureInputFileIsAvailable(day = n)) {
        collectSolutions(n, block).verifyAndRun(input = Input(day = n))
    } else {
        println("Input file is not available")
    }
}

private inline fun collectSolutions(
    day: Int,
    block: Sheet.() -> Unit
) = Sheet(day = day).apply(block)

private inline fun Sheet.verifyAndRun(input: Input) {
    println("Day $day")
    parts.forEach { part ->
        val result = part.evaluate(
            input = input,
            testOnly = breakAfterTest,
            tests = tests.filter { it.partId == part.partId }
        )
        print("answer #${part.number}: ")
        result
            .onSuccess {
                println("${it.output} (${it.time.inWholeMilliseconds}ms)")
            }
            .onFailure {
                println(it.message)
            }
        if (tests.isNotEmpty()) println()
    }
}

private inline fun Part.evaluate(
    input: Input,
    testOnly: Boolean,
    tests: List<Test>
): Result<Answer> {
    if (tests.isNotEmpty()) println("Verifying Part #${number}")

    val testsPassed = tests.all {
        val testInput = it.input
        val result = runWithTimer(testInput)
        val testPassed = result.output == it.expected

        print("[${if (testPassed) "PASS" else "FAIL"}]")
        print(" Input: ${testInput.lines}")
        println()
        if (!testPassed) {
            println("Expected: ${it.expected}")
            println("Actual: ${result.output}")
        }

        testPassed
    }

    if (!testsPassed) return failure("One or more tests failed.")

    if (testOnly) return failure("Break added")

    return try {
        val result = runWithTimer(input)
        if (expected == null || result.output == expected) {
            success(result)
        } else {
            failure("FAIL Expected:$expected actual:${result.output}")
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        failure(e)
    }
}

private inline fun Part.runWithTimer(input: Input) =
    measureTimedValue { algorithm(input) }
        .let { result -> Answer(result.value, result.duration) }

private inline fun success(answer: Answer) = Result.success(answer)
private inline fun failure(message: String) = Result.failure<Answer>(AssertionError(message))
private inline fun failure(throwable: Throwable) = Result.failure<Answer>(throwable)
