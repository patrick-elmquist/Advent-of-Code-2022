import day.Input
import day.day
import day.single
import util.Point
import kotlin.math.abs

// answer #1: 3114
// answer #2: 1540804597682

fun main() {
    day(n = 17) {
        part1(expected = 3114) { input ->
            var shape = Shape.Minus
            var position = Point(2, -4)
            val winds = input.parseGasJets()
            var windIndex = 0
            val mass = List(7) { Point(it, 0) }.toMutableSet()
            var rock = 1
            var moveSide = true
            while (rock < 2023) {
                var blocked = false
                if (moveSide) {
                    val wind = winds[windIndex % winds.size]

                    val newX = (position.x + wind).coerceIn(0, 7 - shape.width)
                    val newPosition = position.copy(x = newX)
                    if (shape.translate(newPosition).none { it in mass }) {
                        position = newPosition
                    }
                    windIndex++
                    moveSide = false
                } else {
                    val newPosition = position.copy(y = position.y + 1)
                    if (shape.translate(newPosition).none { it in mass }) {
                        position = newPosition
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

            abs(mass.minOf { it.y.toFloat() }.toInt())
        }
        part1 test 1 expect 3068

        part2(expected = 1540804597682) { input ->
            var shape = Shape.Minus
            var position = Point(2, -4)
            val winds = input.parseGasJets()
            var windIndex = 0

            val states = mutableMapOf<String, Int>()
            val heights = mutableListOf<Int>()
            val mass = List(7) { Point(it, 0) }.toMutableSet()
            var rock = 1
            var moveSide = true
            var indexWhenStarting = 0
            while (true) {
                var blocked = false
                if (moveSide) {
                    val wind = winds[windIndex % winds.size]

                    val newX = (position.x + wind).coerceIn(0, 7 - shape.width)
                    val newPosition = position.copy(x = newX)
                    if (shape.translate(newPosition).none { it in mass }) {
                        position = newPosition
                    }
                    windIndex++
                    moveSide = false
                } else {
                    val newPosition = position.copy(y = position.y + 1)
                    if (shape.translate(newPosition).none { it in mass }) {
                        position = newPosition
                    } else {
                        blocked = true
                    }
                    moveSide = true
                }

                if (blocked) {
                    if (rock > 250) {
                        val key = key(shape, indexWhenStarting)
                        if (key in states) {
                            break
                        } else {
                            states += key to rock
                        }
                    }
                    mass += shape.translate(position)
                    val height = abs(mass.minOf { it.y.toFloat() }.toInt())
                    heights.add(height)
                    shape = shape.next()
                    position = Point(x = 2, y = mass.minOf { it.y } - 4)
                    rock++
                    moveSide = true
                    indexWhenStarting = windIndex % winds.size
                }
            }

            val key = key(shape, indexWhenStarting)
            val rockNumber = states.getValue(key)
            val heightBeforePattern = heights[rockNumber - 2]
            val other = heights.last()
            val heightPerPattern = other - heightBeforePattern

            val bigint = 1_000_000_000_000
            val mid = bigint - (rockNumber - 1)

            val countPerPattern = heights.drop(rockNumber - 1).count()
            val coveredByPattern = mid / countPerPattern
            val after = mid % countPerPattern

            val t = heights.drop(rockNumber - 2)
                .take(after.toInt() + 1).let { it.last() - it.first() }

            heightBeforePattern + coveredByPattern * heightPerPattern + t
        }
        part2 test 1 expect 1514285714288
    }
}

private fun Input.parseGasJets() = single().map { if (it == '<') -1 else 1 }

private fun key(shape: Shape, index: Int): String = shape.name + index

private fun Shape.translate(position: Point): List<Point> = points.map { it + position }

private fun Shape.next() = when (this) {
    Shape.Minus -> Shape.Plus
    Shape.Plus -> Shape.Corner
    Shape.Corner -> Shape.Line
    Shape.Line -> Shape.Square
    Shape.Square -> Shape.Minus
}

private enum class Shape(val points: List<Point>) {
    Minus(listOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(3, 0))),
    Plus(listOf(Point(1, 0), Point(0, -1), Point(1, -1), Point(2, -1), Point(1, -2))),
    Corner(listOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(2, -1), Point(2, -2))),
    Line(listOf(Point(0, -3), Point(0, -2), Point(0, -1), Point(0, 0))),
    Square(listOf(Point(0, 0), Point(1, 0), Point(0, -1), Point(1, -1)));

    val width = points.maxOf { it.x } - points.minOf { it.x } + 1

}
