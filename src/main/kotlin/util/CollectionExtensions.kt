package util

fun List<String>.sliceByBlank() =
    sliceBy(excludeMatch = true) { _, line -> line.isEmpty() }

fun List<String>.sliceBy(
    excludeMatch: Boolean = false,
    breakCondition: (Int, String) -> Boolean
) = indices.asSequence()
    .filter { i -> breakCondition(i, get(i)) }
    .drop(if (excludeMatch) 0 else 1)
    .plus(size)
    .fold(mutableListOf<List<String>>() to 0) { (list, start), end ->
        list.add(subList(start, end))
        if (excludeMatch) {
            list to end + 1
        } else {
            list to end
        }
    }
    .first
    .toList()

fun <T> Map<Point, T>.minMax(block: (Map.Entry<Point, T>) -> Int): IntRange {
    val max = maxOf { block(it) }
    val min = minOf { block(it) }
    return min..max
}