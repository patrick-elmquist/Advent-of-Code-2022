import day.day
import util.sliceByBlank

// answer #1: 5717
// answer #2: 25935

fun main() {
    day(n = 13) {
        part1(expected = 5717) { input ->
            input.lines.sliceByBlank()
                .map { it.map(::parse) }
                .mapIndexed { index, (left, right) ->
                    if (left < right) index + 1 else 0
                }.sum()
        }
        part1 test 1 expect 13

        part2(expected = 25935) { input ->
            val packets = input.lines.filter { it.isNotBlank() }.map(::parse)
            val dividerTwo = TokenList(Raw(2))
            val dividerSix = TokenList(Raw(6))
            val sorted = (packets + dividerTwo + dividerSix).sorted()
            (sorted.indexOf(dividerTwo) + 1) * (sorted.indexOf(dividerSix) + 1)

        }
        part2 test 1 expect 140
    }
}

private fun parse(line: String): Token {
    val trimmed = line.removeSurrounding("[", "]")
    if (trimmed.isEmpty()) return TokenList(emptyList())
    trimmed.toIntOrNull()?.let { return Raw(it) }
    val tokens = mutableListOf<Token>()
    var remaining = trimmed
    while (remaining.isNotEmpty()) {
        remaining = when {
            remaining.startsWith(',') -> {
                remaining.drop(1)
            }

            remaining.startsWith("[") -> {
                var level = 0
                var index = -1
                for (i in remaining.indices) {
                    when (remaining[i]) {
                        '[' -> {
                            level++
                        }

                        ']' -> {
                            level--
                            if (level == 0) {
                                index = i
                                break
                            }
                        }
                    }
                }
                val substring = remaining.substring(0, index + 1)
                tokens.add(parse(substring))
                remaining.drop(substring.length)
            }

            else -> {
                val substring = remaining.substringBefore(',')
                tokens.add(Raw(substring.toInt()))
                remaining.drop(substring.length)
            }
        }
    }
    return TokenList(tokens)
}

private sealed class Token : Comparable<Token> {
    override fun compareTo(other: Token): Int =
        when {
            this is Raw && other is Raw -> {
                value - other.value
            }

            this is TokenList && other is TokenList -> {
                content.zip(other.content).forEach { (l, r) ->
                    val compare = l.compareTo(r)
                    if (compare != 0) return compare
                }
                content.size - other.content.size
            }

            this is Raw && other is TokenList -> {
                TokenList(this).compareTo(other)
            }

            this is TokenList && other is Raw -> {
                compareTo(TokenList(other))
            }

            else -> {
                error("left:$this right:$other")
            }
        }
}

private data class Raw(val value: Int) : Token()
private data class TokenList(val content: List<Token>) : Token() {
    constructor(vararg tokenList: Token) : this(tokenList.toList())
}