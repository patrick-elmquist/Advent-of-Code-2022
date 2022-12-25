import day.day
import util.log

// answer #1: 20-==01-2-=1-2---1-0
// answer #2:

private fun decimalMap(n: Long): String {
    return when (n) {
        0L -> "0"
        1L -> "1"
        2L -> "2"
        3L -> "1="
        4L -> "1-"
        5L -> "10"
        6L -> "11"
        7L -> "12"
        8L -> "2="
        9L -> "2-"
        else -> error("")
    }
}

fun main() {
    day(n = 25) {
        check(translate("1") == 1L)
        check(translate("2") == 2L)
        check(translate("1=") == 3L)
        check(translate("1-") == 4L)
        check(translate("10") == 5L)
        check(translate("1=11-2") == 2022L)
        check(translate("2=-1=0") == 4890L)
        check(translate("1121-1110-1=0") == 314159265L)
        check(reverse(1747L) == "1=-0-2")
        check(reverse(906L) == "12111")
        check(reverse(12345L) == "1-0---0")
        check(reverse(314159265L) == "1121-1110-1=0")
        check(reverse(198L) == "2=0=")

        var test = 4980
        buildString {
            while (test > 0) {
                append(test % 5)
                test /= 5
            }
        }.reversed().log("out")


        part1(expected = "20-==01-2-=1-2---1-0") { input ->
            val sum = input.lines.map { translate(it) }.sumOf {
                it
            }.log("sum")
//            reverse(2022).log("2022")
            reverse(sum)
        }
//        part1 test 1 expect "2=-1=0"

        part2 { input ->

        }
        part2 test 1 expect Unit
    }
}

private fun reverse(value: Long): String {
    val list = buildList {
        var n = value
        while (n > 0) {
            val r = n % 5
            add(decimalMap(r))
            n /= 5
        }
    }.reversed().log("value:$value mid:")

    val new = mutableListOf<String>()
    var remainder = 0
    list.reversed().forEach {
        when(it) {
            "0",
            "1" -> {
                new.add((it.toInt() + remainder).toString())
                remainder = 0
            }
            "2" -> {
                val element = decimalMap(it.toLong() + remainder)
                remainder = 0
                when (element) {
                    "2" -> {
                       new.add(element)
                        remainder = 0
                    }
                    "1=" -> {
                        val i = -2 + remainder
                        new.add(if (i == -1) "-" else if (i == -2) "=" else "$i")
                        remainder = 1
                    }
                    "1-" -> {
                        val i = -1 + remainder
                        new.add(if (i == -1) "-" else "$i")
                        remainder = 1
                    }
                }
            }

            "1=" -> {
                val i = -2 + remainder
                new.add(if (i == -1) "-" else if (i == -2) "=" else "$i")
                remainder = 1
            }
            "1-" -> {
                val i = -1 + remainder
                new.add(if (i == -1) "-" else "$i")
                remainder = 1
            }
            else -> error("")
        }
        new.last().log("it:$it r:$remainder out:")
    }
    if (remainder != 0) new.add(remainder.toString())
    return new.reversed().joinToString("").log("snafu:")
}

private fun translate(snafu: String): Long {
    var base = 1L
    var remaining = snafu.reversed()
    var sum = 0L
    while (remaining.isNotEmpty()) {
        val n = remaining.first()

        val total = when (n) {
            '=' -> -2L
            '-' -> -1L
            '0' -> 0L
            '1' -> 1L
            '2' -> 2L
            else -> error("$n")
        }
        sum += total * base
        base *= 5
        remaining = remaining.drop(1)
    }
    return sum
}