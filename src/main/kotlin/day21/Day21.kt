package day21

import common.Input
import common.day

// answer #1: 41857219607906
// answer #2: 3916936880448

fun main() {
    day(n = 21) {
        part1(expected = 41857219607906L) { input ->
            val monkeys = input.parseMonkeys().toMutableMap()
            val root = monkeys.getValue("root")
            root.tryResolve(monkeys) ?: error("should be able to resolve")
        }
        part1 test 1 expect 152L

        part2(expected = 3916936880448L) { input ->
            val monkeys = input.parseMonkeys().toMutableMap()
            monkeys.remove("humn")

            val root = monkeys.getValue("root") as Number.Expression
            root.tryResolve(monkeys)

            val leftBranch = monkeys.getValue(root.left)
            val rightBranch = monkeys.getValue(root.right)

            when {
                leftBranch is Number.Raw ->
                    solveEquation(
                        endResult = leftBranch.n,
                        firstExpression = rightBranch as Number.Expression,
                        monkeys = monkeys
                    )

                rightBranch is Number.Raw -> {
                    solveEquation(
                        endResult = rightBranch.n,
                        firstExpression = leftBranch as Number.Expression,
                        monkeys = monkeys
                    )
                }
                else -> error("")
            }
        }
        part2 test 1 expect 301L
    }
}

private fun solveEquation(
    endResult: Long,
    firstExpression: Number,
    monkeys: MutableMap<String, Number>
): Long {
    var leftSide = endResult
    var nextExpression = firstExpression
    while (true) {
        val current = nextExpression as Number.Expression

        val leftBranch = monkeys[current.left]
        val rightBranch = monkeys[current.right]

        if (leftBranch == null) {
            val raw = (rightBranch as Number.Raw).n
            when (current.command) {
                "+" -> leftSide -= raw
                "*" -> leftSide /= raw
                "-" -> leftSide += raw
                "/" -> leftSide *= raw
            }
            return leftSide
        }

        if (rightBranch == null) {
            val raw = (leftBranch as Number.Raw).n
            when (current.command) {
                "+" -> leftSide -= raw
                "*" -> leftSide /= raw
                "-" -> leftSide -= raw
                "/" -> leftSide = raw / leftSide
            }
            return leftSide
        }

        val (raw, expression) = when {
            leftBranch is Number.Raw -> leftBranch.n to rightBranch
            rightBranch is Number.Raw -> rightBranch.n to leftBranch
            else -> error("")
        }

        when (current.command) {
            "+" -> leftSide -= raw
            "*" -> leftSide /= raw
            "-" -> {
                if (leftBranch is Number.Raw) {
                    leftSide -= raw
                    leftSide *= -1
                } else {
                    leftSide += raw
                }
            }

            "/" -> {
                if (leftBranch is Number.Raw) {
                    leftSide = raw / leftSide
                } else {
                    leftSide *= raw
                }
            }
        }
        nextExpression = expression
    }
}

private sealed class Number {
    abstract fun tryResolve(monkeys: MutableMap<String, Number>): Long?

    data class Expression(val left: String, val right: String, val command: String) : Number() {
        override fun tryResolve(monkeys: MutableMap<String, Number>): Long? {
            val leftResolved = monkeys.tryResolveAndMap(left)
            val rightResolved = monkeys.tryResolveAndMap(right)
            if (leftResolved == null || rightResolved == null) return null
            return when (command) {
                "+" -> leftResolved + rightResolved
                "-" -> leftResolved - rightResolved
                "/" -> leftResolved / rightResolved
                "*" -> leftResolved * rightResolved
                else -> error("")
            }
        }

        private fun MutableMap<String, Number>.tryResolveAndMap(name: String) =
            get(name)?.tryResolve(this)?.also { this[name] = Raw(it) }
    }

    data class Raw(val n: Long) : Number() {
        override fun tryResolve(monkeys: MutableMap<String, Number>): Long = n
    }
}

private fun Input.parseMonkeys(): Map<String, Number> =
    lines.associate {
        val split = it.split(" ")
        split.first().dropLast(1) to if (split.size == 2) {
            Number.Raw(split[1].toLong())
        } else {
            Number.Expression(split[1], split[3], split[2])
        }
    }
