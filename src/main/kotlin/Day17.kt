import day.day
import day.single
import util.Point
import util.log
import kotlin.math.abs

// answer #1:
// answer #2:

fun main() {
    day(n = 17) {
        part1(expected = 3114) { input ->
            var shape = Shape.Minus
            val n = 2022
            var position = Point(2, -4)
            val floor = List(7) { Point(it, 0) }
            val winds = input.single()
            var windIndex = 0

            val mass = mutableSetOf<Point>()
            mass.addAll(floor)
            var rock = 1
//            print(floor, position, shape)
            var moveSide = true
            while (rock < 2023) {
                var blocked = false
                if (moveSide) {
                    val wind = when (winds[windIndex]) {
                        '<' -> -1
                        else -> 1
                    }

                    val newX = (position.x + wind).coerceIn(0, 7 - shape.width)
//                    println("x:${position.x} wind:$wind width:${shape.width} newX:$newX")
                    if (shape.translate(position.copy(x = newX)).none { it in mass }) {
                        position = position.copy(x = newX)

                        windIndex = (windIndex + 1) % winds.length

//                        println("side ${if (wind == -1) "left" else "right"} index:$windIndex")
//                        println(winds.take(windIndex))
//                        println(winds)
                    } else {
//                        println("blocked on moving to the side")
                        windIndex = (windIndex + 1) % winds.length
//                        blocked = true
                    }
                    moveSide = false
                } else {
                    val newY = (position.y + 1)
                    if (shape.translate(position.copy(y = newY)).none { it in mass }) {
                        position = position.copy(y = newY)
//                        println("down")
                    } else {
//                        println("blocked on going down")
                        blocked = true
                    }
                    moveSide = true
                }

                if (rock == 49) {
//                    print(mass, position, shape, false)
                }

//                rock.log("blocked:$blocked")
//                if (rock == 49) print(floor, position, shape, false)
                if (blocked) {
                    mass += shape.translate(position)
//                    floor = shape.newFloorP(floor, position)
                    shape = shape.next()
                    position = Point(x = 2, y = mass.minOf { it.y } - 4)
                    rock++
//                    if (rock == 49) print(floor, position, shape)
                    moveSide = true
//                    print(mass, position, shape, true)
                }
            }

            abs(mass.minOf {
                it.y.toFloat()
            }.toInt())
        }
        part1 test 1 expect 3068

        part2 { input ->
            var shape = Shape.Minus
            val n = 2022
            var position = Point(2, -4)
            val floor = List(7) { Point(it, 0) }
            val winds = input.single()
            var windIndex = 0

            val mass = mutableSetOf<Point>()
            mass.addAll(floor)
            var rock = 1
//            print(floor, position, shape)
            var moveSide = true
            while (rock < 2023) {
                var blocked = false
                if (moveSide) {
                    val wind = when (winds[windIndex]) {
                        '<' -> -1
                        else -> 1
                    }

                    val newX = (position.x + wind).coerceIn(0, 7 - shape.width)
//                    println("x:${position.x} wind:$wind width:${shape.width} newX:$newX")
                    if (shape.translate(position.copy(x = newX)).none { it in mass }) {
                        position = position.copy(x = newX)

                        windIndex = (windIndex + 1) % winds.length

//                        println("side ${if (wind == -1) "left" else "right"} index:$windIndex")
//                        println(winds.take(windIndex))
//                        println(winds)
                    } else {
//                        println("blocked on moving to the side")
                        windIndex = (windIndex + 1) % winds.length
//                        blocked = true
                    }
                    moveSide = false
                } else {
                    val newY = (position.y + 1)
                    if (shape.translate(position.copy(y = newY)).none { it in mass }) {
                        position = position.copy(y = newY)
//                        println("down")
                    } else {
//                        println("blocked on going down")
                        blocked = true
                    }
                    moveSide = true
                }

                if (rock == 49) {
//                    print(mass, position, shape, false)
                }

//                rock.log("blocked:$blocked")
//                if (rock == 49) print(floor, position, shape, false)
                if (blocked) {
                    mass += shape.translate(position)
//                    floor = shape.newFloorP(floor, position)
                    shape = shape.next()
                    position = Point(x = 2, y = mass.minOf { it.y } - 4)
                    rock++
//                    if (rock == 49) print(floor, position, shape)
                    moveSide = true
//                    print(mass, position, shape, true)
                }
            }

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
    val height = points.maxOf { it.y } - points.minOf { it.y } + 1

    fun newFloorP(oldFloor: List<Point>, position: Point): List<Point> {
        val translated = translate(position)
        return buildList {
            val map = (0 until 7)
                .map { x -> translated.filter { it.x == x }.minByOrNull { it.y } ?: oldFloor[x] }
            map
                .zipWithNext()
                .onEach { (a, b) ->
                    add(a)
                    when {
                        b.y == a.y -> Unit
                        b.y < a.y -> Unit
                        else -> {
                            for (y in a.y + 1 .. b.y) {
                                add(Point(a.x, y))
                            }
                        }
                    }
                }
                map.takeLast(2).let { (a, b) ->
                    add(b)
                    when {
                        b.y == a.y -> Unit
                        b.y < a.y -> Unit
                        else -> {
                            for (y in a.y + 1 .. b.y) {
                                add(Point(b.x, y))
                            }
                        }
                    }
                }
//            for (x in 0 until 7) {
//                val element = translated.filter { it.x == x }.minByOrNull { it.y } ?: oldFloor[x]
//                add(element)
//            }
        }
    }

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
