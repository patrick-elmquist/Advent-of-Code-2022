import day.day

// answer #1: 20-==01-2-=1-2---1-0
// answer #2: no answer for 25th P2

fun main() {
    day(n = 25) {
        part1(expected = "20-==01-2-=1-2---1-0") { input ->
            val decimalSum = input.lines.sumOf { snafuToDecimal(it) }
            decimalToSnafu(decimalSum)
        }

        part2 { input ->
            // nothing to do here for the 25th
        }
    }
}

private fun decimalToBase5(n: Long) =
    when (n) {
        0L -> "0"
        1L -> "1"
        2L -> "2"
        3L -> "1="
        4L -> "1-"
        else -> error("")
    }

private fun decimalToSnafu(value: Long): String {
    val list = buildList {
        var n = value
        while (n > 0) {
            val r = n % 5
            add(decimalToBase5(r))
            n /= 5
        }
    }.reversed()

    val new = mutableListOf<String>()
    var remainder = 0
    list.reversed().forEach {
        when (it) {
            "0",
            "1" -> {
                new.add((it.toInt() + remainder).toString())
                remainder = 0
            }

            "2" -> {
                val element = decimalToBase5(it.toLong() + remainder)
                remainder = 0
                when (element) {
                    "2" -> new.add(element)
                    "1=" -> {
                        new.add("=")
                        remainder = 1
                    }

                    "1-" -> {
                        new.add("-")
                        remainder = 1
                    }
                }
            }

            "1=" -> {
                val i = -2 + remainder
                new.add(if (i == -1) "-" else "=")
                remainder = 1
            }

            "1-" -> {
                val i = -1 + remainder
                new.add(if (i == -1) "-" else "$i")
                remainder = 1
            }

            else -> error("")
        }
    }
    if (remainder != 0) new.add(remainder.toString())
    return new.reversed().joinToString("")
}

private fun snafuToDecimal(snafu: String): Long {
    var remaining = snafu.reversed()
    var base = 1L
    var sum = 0L
    while (remaining.isNotEmpty()) {
        val n = when (remaining.first()) {
            '=' -> -2L
            '-' -> -1L
            '0' -> 0L
            '1' -> 1L
            '2' -> 2L
            else -> error("${remaining.first()}")
        }
        sum += n * base
        base *= 5
        remaining = remaining.drop(1)
    }
    return sum
}