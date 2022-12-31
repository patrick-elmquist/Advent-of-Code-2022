package common.util

/**
 * Class representing a point in 2 dimensions
 */
data class Point(val x: Int, val y: Int) {
    constructor(x: String, y: String) : this(x.toInt(), y.toInt())

    operator fun plus(point: Point) = Point(x + point.x, y + point.y)
    operator fun minus(point: Point) = Point(x - point.x, y - point.y)

    companion object
}

data class Point3D(val x: Int, val y: Int, val z: Int) {
    constructor(x: String, y: String, z: String) : this(x.toInt(), y.toInt(), z.toInt())

    operator fun plus(point: Point) = Point(x + point.x, y + point.y)
    operator fun minus(point: Point) = Point(x - point.x, y - point.y)

    companion object
}

fun Point.neighbors(
    diagonal: Boolean = false,
    includeSelf: Boolean = false
) = sequence {
    if (diagonal) yield(Point(x - 1, y - 1))
    yield(copy(y = y - 1))
    if (diagonal) yield(Point(x + 1, y - 1))

    yield(copy(x = x - 1))
    if (includeSelf) yield(this@neighbors)
    yield(copy(x = x + 1))

    if (diagonal) yield(Point(x - 1, y + 1))
    yield(copy(y = y + 1))
    if (diagonal) yield(Point(x + 1, y + 1))
}

fun Point.neighborsContainPoint(
    point: Point,
    diagonal: Boolean = false,
    includeSelf: Boolean = false
): Boolean {
    if (point == this) {
        return includeSelf
    }

    val (px, py) = point
    val inX = px in (x - 1..x + 1)
    val inY = py in (y - 1..y + 1)
    if (diagonal) {
        return inX && inY
    }

    return px == x && inY || py == y && inX
}

fun Point3D.neighbors() = sequence {
    yield(copy(y = y - 1))
    yield(copy(y = y + 1))

    yield(copy(x = x - 1))
    yield(copy(x = x + 1))

    yield(copy(z = z - 1))
    yield(copy(z = z + 1))
}

