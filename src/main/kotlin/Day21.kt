import day.Input
import day.day

// answer #1: 41857219607906
// answer #2: 3916936880448

fun main() {
    day(n = 21) {
        part1(expected = 41857219607906L) { input ->
            val monkeys = input.parseMonkeys()
            val root = monkeys.getValue("root")
            root.tryResolve(monkeys.toMutableMap()) ?: error("")
        }
        part1 test 1 expect 152L

        part2(expected = 3916936880448L) { input ->
            val me = "humn"
            val monkeys = input.parseMonkeys().toMutableMap()

            val root = monkeys.getValue("root") as Number.Expression
            monkeys.remove("root")
            monkeys.remove(me)

            val a = monkeys.getValue(root.a)
            val b = monkeys.getValue(root.b)

            a.tryResolve(monkeys)

            var start = a
            var right = b.tryResolve(monkeys)
                ?: error("could not resolve b")
            while (right != 0L) {
                val current = start as Number.Expression

                if (current.a == me) {
                    val raw = (monkeys.getValue(current.b) as Number.Raw).n
                    when (current.command) {
                        "+" -> right -= raw
                        "-" -> right += raw
                        "/" -> right *= raw
                        "*" -> right /= raw
                    }
                    break
                }
                if (current.b == me) {
                    val raw = (monkeys.getValue(current.a) as Number.Raw).n
                    when (current.command) {
                        "+" -> right -= raw
                        "-" -> right -= raw
                        "/" -> right = raw / right
                        "*" -> right /= raw
                    }
                    break
                }

                val ca = monkeys.getValue(current.a)
                val cb = monkeys.getValue(current.b)

                val (raw, expression) = when {
                    ca is Number.Raw -> ca.n to cb
                    cb is Number.Raw -> cb.n to ca
                    else -> error("")
                }

                when (current.command) {
                    "+" -> right -= raw
                    "-" -> {
                        if (ca is Number.Raw) {
                            right -= raw
                            right *= -1
                        } else {
                            right += raw
                        }
                    }

                    "/" -> {
                        if (ca is Number.Raw) {
                            right = raw / right
                        } else {
                            right *= raw
                        }
                    }

                    "*" -> right /= raw
                }
                start = expression
            }
            right
        }
        part2 test 1 expect 301L
    }
}

private fun Input.parseMonkeys(): Map<String, Number> =
    lines.associate {
        val split = it.split(" ")
        split.first().dropLast(1) to if (split.size == 2) {
            val n = split.last().toLong()
            Number.Raw(n)
        } else {
            val a = split[1]
            val sign = split[2]
            val b = split[3]
            Number.Expression(a, b, sign)
        }
    }

private sealed class Number {
    abstract fun tryResolve(monkeys: MutableMap<String, Number>): Long?

    data class Expression(val a: String, val b: String, val command: String) : Number() {
        override fun tryResolve(monkeys: MutableMap<String, Number>): Long? {
            val aInt = tryResolveAndCache(monkeys, a)
            val bInt = tryResolveAndCache(monkeys, b)
            if (aInt == null || bInt == null) return null
            return when (command) {
                "+" -> aInt + bInt
                "-" -> aInt - bInt
                "/" -> aInt / bInt
                "*" -> aInt * bInt
                else -> error("")
            }
        }

        private fun tryResolveAndCache(monkeys: MutableMap<String, Number>, a: String): Long? {
            val aInt = when (val monkeyA = monkeys[a]) {
                null -> null
                is Expression -> {
                    monkeyA.tryResolve(monkeys)?.also { monkeys[a] = Raw(it) }
                }

                is Raw -> {
                    monkeyA.tryResolve(monkeys)
                }
            }
            return aInt
        }
    }

    data class Raw(val n: Long) : Number() {
        override fun tryResolve(monkeys: MutableMap<String, Number>): Long = n
    }
}