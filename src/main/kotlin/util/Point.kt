package util

/**
 * Class representing a point in 2 dimensions
 */
data class Point(val x: Int, val y: Int) {
    constructor(x: String, y: String) : this(x.toInt(), y.toInt())

    operator fun plus(point: Point) = Point(x + point.x, y + point.y)
    operator fun minus(point: Point) = Point(x - point.x, y - point.y)

    companion object
}

fun Point.isValid(maxX: Int, maxY: Int): Boolean {
    if (x < 0 || x > maxX) return false
    if (y < 0 || y > maxY) return false
    return true
}

fun Point.neighbors(
    diagonal: Boolean = false,
    includeSelf: Boolean = false
): List<Point> =
    buildList {
        if (diagonal) add(Point(x - 1, y - 1))
        add(copy(y = y - 1))
        if (diagonal) add(Point(x + 1, y - 1))

        add(copy(x = x - 1))
        if (includeSelf) add(this@neighbors)
        add(copy(x = x + 1))

        if (diagonal) add(Point(x - 1, y + 1))
        add(copy(y = y + 1))
        if (diagonal) add(Point(x + 1, y + 1))
    }
