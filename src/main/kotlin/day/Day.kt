@file:Suppress("NOTHING_TO_INLINE")

package day

import kotlin.time.measureTimedValue

fun day(
    n: Int,
    block: Sheet.() -> Unit
) = collectSolutions(block).verifyAndRun(input = Input(day = n))

private inline fun collectSolutions(block: Sheet.() -> Unit): Sheet =
    Sheet().apply(block)

private inline fun Sheet.verifyAndRun(input: Input) {
    parts.forEach { part ->
        print("Running Part #${part.number}...")
        val result = part.evaluate(input)
        print("Answer: ")
        result
            .onSuccess {
                println("${it.output} (${it.time.inWholeMilliseconds}ms)")
            }
            .onFailure {
                println(it.message)
            }
        if (part.tests.isNotEmpty()) println()
    }
}

private inline fun Part.evaluate(
    input: Input
): Result<Answer> {
    if (tests.isNotEmpty()) println("Verifying Part #${number}")

    val testsPassed = tests.all {
        val testInput = Input(it.input.lines())
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

private inline fun Part.runWithTimer(input: Input): Answer =
    measureTimedValue { algorithm(input) }.let { result -> Answer(number, result.value, result.duration) }

private inline fun success(answer: Answer) = Result.success(answer)
private inline fun failure(message: String) = Result.failure<Answer>(AssertionError(message))
private inline fun failure(throwable: Throwable) = Result.failure<Answer>(throwable)
