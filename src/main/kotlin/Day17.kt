import day.day
import day.single
import util.Point
import util.log
import java.math.BigInteger
import kotlin.math.abs

// answer #1: 3114
// answer #2: 1540804597682
// not 1541294964005
// not 1540143884869
// not 1539481268010

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

        part2(expected = BigInteger.valueOf(1540804597682)) { input ->
            var shape = Shape.Minus
            var position = Point(2, -4)
            val winds = input.single()
            var windIndex = 0

//            val remaining = BigInteger("1000000000000") - BigInteger.valueOf(23)
//            remaining.log("remaining")
//            val coveredByPattern = remaining / BigInteger.valueOf(35)
//            coveredByPattern.log("covered")
//            val left = remaining % BigInteger.valueOf(35)
//            left.log("left")
//
//            val result = BigInteger.valueOf(23) + BigInteger.valueOf(53) * coveredByPattern + BigInteger.valueOf(44)
//            result.log("result")

            val states = mutableMapOf<String, Int>()
            val heights = mutableListOf<Int>()
            val mass = List(7) { Point(it, 0) }.toMutableSet()
            var rock = 1
            var moveSide = true
            var indexWhenStarting = 0
            val foundDuplicates = mutableMapOf<String, Int>()
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
                    if (rock > 14000) {

                        val key = key(shape, indexWhenStarting, mass)
                    if (key in states) {
//                        "duplicate $rock seen:${states[key]} with key:$key".log()
//                        "found duplicate, same as ${states[key]}".log() 1540804597682
                        break
                        if (key in foundDuplicates) {
                            if (foundDuplicates.getValue(key) == 1) {
                            } else {
                                foundDuplicates[key] = foundDuplicates.getValue(key) + 1
                            }
                        } else {
                            foundDuplicates += key to 1
                        }
                    } else {
                        states += key to rock
                    }
                    }
                    mass += shape.translate(position)
                    val height = abs(mass.minOf { it.y.toFloat() }.toInt())
                    heights.add(height)
//                    "adding $rock seen:${states[key]} with key:$key height:$height".log()
                    shape = shape.next()
                    position = Point(x = 2, y = mass.minOf { it.y } - 4)
                    rock++
                    moveSide = true
                    indexWhenStarting = windIndex
                }
            }

            val key = key(shape, indexWhenStarting, mass)
            val rockNumber = states.getValue(key).log("index")
            val heightBeforePattern = heights.get(rockNumber - 2).log("height").toBigInteger()
            val other = heights.last().log("other")
            val heightPerPattern = other.toBigInteger() - heightBeforePattern // could be that it should be adjusted up or down
//            heightPerPattern.log("height per pattern")

            val bigint = BigInteger("1000000000000")
            val mid = bigint - (rockNumber - 1).toBigInteger()

            val countPerPattern = heights.drop(rockNumber - 1).count().log("pattern len")
            val coveredByPattern = mid / countPerPattern.toBigInteger()
            val after = mid % countPerPattern.toBigInteger()
//            after.log("after")

            val t = heights.drop(rockNumber - 2)
                .take(after.toInt() + 1).let { it.last() - it.first() }
                .toBigInteger()

            heightBeforePattern + coveredByPattern * heightPerPattern + t
        }
        part2 test 1 expect BigInteger("1514285714288")
    }
}

private fun key(shape: Shape, index: Int, mass: Set<Point>): String {
    val minY = mass.minOf { it.y }
    val s = buildString {
        for (x in (0 until 7)) {
            val p = mass.find { it.x == x && it.y == minY }
            if (p == null) {
                append(" ")
            } else {
                append("#")
            }
        }
    }
    return shape.name + index + s
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
