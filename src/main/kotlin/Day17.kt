import day.day
import day.single
import util.Point
import util.log
import java.math.BigInteger
import kotlin.math.abs

// answer #1:
// answer #2:

fun main() {
    day(n = 17) {
        part1(expected = 3114) { input ->
            var shape = Shape.Minus
            val n = 2022
            var position = Point(2, -4)
            val winds = input.single()
            var windIndex = 0
            val mass = List(7) { Point(it, 0) }.toMutableSet()
            var rock = 1
            var moveSide = true
            while (rock <= n) {
                var blocked = false
                if (moveSide) {
                    val wind = when (winds[windIndex]) {
                        '<' -> -1
                        else -> 1
                    }

                    val newX = (position.x + wind).coerceIn(0, 7 - shape.width)
                    if (shape.translate(position.copy(x = newX)).none { it in mass }) {
                        position = position.copy(x = newX)
                    }
                    windIndex = (windIndex + 1) % winds.length
                    moveSide = false
                } else {
                    val newY = (position.y + 1)
                    if (shape.translate(position.copy(y = newY)).none { it in mass }) {
                        position = position.copy(y = newY)
                    } else {
                        blocked = true
                    }
                    moveSide = true
                }

                if (blocked) {
                    mass += shape.translate(position)
                    shape = shape.next()
                    position = Point(x = 2, y = mass.minOf { it.y } - 4)
                    rock++
                    moveSide = true
                }
            }

            abs(mass.minOf {
                it.y.toFloat()
            }.toInt())
        }
        part1 test 1 expect 3068

        part2 { input ->
            var shape = Shape.Minus
            var position = Point(2, -4)
            val winds = input.single()
            var windIndex = 0

            val remaining = BigInteger("1000000000000") - BigInteger.valueOf(23)
            remaining.log("remaining")
            val coveredByPattern = remaining / BigInteger.valueOf(35)
            coveredByPattern.log("covered")
            val left = remaining % BigInteger.valueOf(35)
            left.log("left")

            val result = BigInteger.valueOf(23) + BigInteger.valueOf(53) * coveredByPattern + BigInteger.valueOf(44)
            result.log("result")

            val states = mutableMapOf<String, Int>()
            val mass = List(7) { Point(it, 0) }.toMutableSet()
            var rock = 1
            var moveSide = true
            var indexWhenStarting = 0
            while (true) {
                var blocked = false
                if (moveSide) {
                    val wind = when (winds[windIndex]) {
                        '<' -> -1
                        else -> 1
                    }

                    val newX = (position.x + wind).coerceIn(0, 7 - shape.width)
                    if (shape.translate(position.copy(x = newX)).none { it in mass }) {
                        position = position.copy(x = newX)
                    }
                    windIndex = (windIndex + 1) % winds.length
                    moveSide = false
                } else {
                    val newY = (position.y + 1)
                    if (shape.translate(position.copy(y = newY)).none { it in mass }) {
                        position = position.copy(y = newY)
                    } else {
                        blocked = true
                    }
                    moveSide = true
                }

                if (blocked) {
                    val key = shape.name + indexWhenStarting
                    if (key in states) {
//                        "duplicate $rock seen:${states[key]} with key:$key".log()
                    } else {
                        states += key to rock
                    }
                    val height = abs(mass.minOf { it.y.toFloat() }.toInt())
                    "duplicate $rock seen:${states[key]} with key:$key height:$height".log()
                    mass += shape.translate(position)
                    shape = shape.next()
                    position = Point(x = 2, y = mass.minOf { it.y } - 4)
                    rock++
                    moveSide = true
                    indexWhenStarting = windIndex
                }
            }


//            23L + coveredByPattern * 53
            abs(mass.minOf {
                it.y.toFloat()
            }.toInt())
        }
        part2 test 1 expect 1514285714288L
    }
}

private fun print(floor: Set<Point>, position: Point, shape: Shape, atBottom: Boolean = false) {
    val s = shape.translate(position)
    val all = s + floor

    val minX = all.minOf { it.x }
    val maxX = all.maxOf { it.x }
    val minY = all.minOf { it.y }
    val maxY = all.maxOf { it.y }

    for (y in minY..maxY) {
        if (y != maxY) {
            print("|")
        } else {
            print("+")
        }
        for (x in minX..maxX) {
            val p = Point(x, y)
            when (p) {
                in s -> {
                    if (atBottom) {
                        print("#")
                    } else {
                        print("@")
                    }
                }

                in floor -> {
                    if (y == 0) {
                        print("-")
                    } else {
                        print("#")
                    }
                }

                else -> {
                    if (y == 0) {
                        print("-")
                    } else {
                        print(".")
                    }
                }
            }
        }
        if (y != maxY) {
            print("|")
        } else {
            print("+")
        }
        println()
    }
    println()
    Thread.sleep(300L)
}

private enum class Shape(val points: List<Point>) {
    Minus(listOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(3, 0))),
    Plus(listOf(Point(1, 0), Point(0, -1), Point(1, -1), Point(2, -1), Point(1, -2))),
    Corner(listOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(2, -1), Point(2, -2))),
    Line(listOf(Point(0, -3), Point(0, -2), Point(0, -1), Point(0, 0))),
    Square(listOf(Point(0, 0), Point(1, 0), Point(0, -1), Point(1, -1)));

    val width = points.maxOf { it.x } - points.minOf { it.x } + 1

    fun translate(position: Point): List<Point> {
        return points.map { it + position }
    }

    fun next() = when (this) {
        Minus -> Plus
        Plus -> Corner
        Corner -> Line
        Line -> Square
        Square -> Minus
    }
}
