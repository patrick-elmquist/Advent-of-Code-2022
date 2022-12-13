
import day.day
import util.sliceByBlank

// answer #1: 5717
// answer #2: 25935

private fun parse(line: String): Token {
//                line.log("parsing...")
    val trimmed = line.removeSurrounding("[", "]")
    // empty
    if (trimmed.isEmpty()) return TokenList(emptyList())
    // value
    trimmed.toIntOrNull()?.let { return Raw(it) }
    // list

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
//                            index.log("remaining:$remaining index:")
                val substring = remaining.substring(0, index + 1)//.log("substring")
                tokens.add(parse(substring))
                remaining.drop(substring.length) // change
                // sub string until closing
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

fun main() {
    day(n = 13) {
        part1(expected = 5717) { input ->
            val pairs = input.lines.sliceByBlank()

            pairs.mapIndexed { index, (left, right) ->
                if (compare(parse(left), parse(right)) < 0) index + 1 else 0
            }.sum()
        }
        part1 test 1 expect 13

        part2(expected = 25935) { input ->
            val filtered = input.lines.filter { it.isNotBlank() }
            val withDividers = filtered + listOf("[[2]]") + listOf("[[6]]")
            val sorted = withDividers
                .map { parse(it) }
                .sortedWith { l, r ->
                    compare(l, r)
                }
            val two = sorted.indexOfFirst {
                it is TokenList && it.content.singleOrNull() == Raw(2)
            } + 1
            val six = sorted.indexOfFirst {
                it is TokenList && it.content.singleOrNull() == Raw(6)
            } + 1
            two * six

        }
        part2 test 1 expect 140
    }
}

private fun compare(left: Token, right: Token): Int {
    return when {
        left is Raw && right is Raw -> {
            left.value - right.value
        }

        left is TokenList && right is TokenList -> {
            left.content.zip(right.content).forEach { (l, r) ->
                val compare = compare(l, r)
                if (compare != 0) return compare
            }
            left.content.size - right.content.size
        }

        left is Raw && right is TokenList -> {
            compare(TokenList(left), right)
        }

        left is TokenList && right is Raw -> {
            compare(left, TokenList(right))
        }

        else -> {
            error("left:$left right:$right")
        }
    }
}

private sealed class Token

private data class Raw(val value: Int) : Token()
private data class TokenList(val content: List<Token>) : Token() {
    constructor(vararg tokenList: Token) : this(tokenList.toList())
}