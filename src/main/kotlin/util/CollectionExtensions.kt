package util

fun <T : Collection<String>> T.toInts() = map { it.toInt() }
fun <T : Collection<String>> T.toLongs() = map { it.toLong() }

fun <T : List<E>, E : CharSequence> T.groupByBlank() =
    (indices.filter { get(it).isEmpty() } + listOf(size))
        .fold(mutableListOf<List<E>>() to 0) { (list, start), end ->
            list.add(subList(start, end))
            list to end + 1
        }.first.toList()
