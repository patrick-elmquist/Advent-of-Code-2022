package common.util

fun <T> Regex.match(input: String, block: (MatchResult.Destructured) -> T) =
    matchEntire(input)?.destructured?.let(block) ?: error("regex not valid for: $input")

