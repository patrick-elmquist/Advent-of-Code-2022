import day.Input
import day.day
import util.Point
import kotlin.math.abs

// answer #1: 3114
// answer #2: 1540804597682

fun main() {
    day(n = 17) {
        val columns = 7
        val startPoint = Point(2, -4)
        val floor = List(columns) { Point(it, 0) }
        val rocks = listOf(
            Rock(Point(0, 0), Point(1, 0), Point(2, 0), Point(3, 0)),
            Rock(Point(1, 0), Point(0, -1), Point(1, -1), Point(2, -1), Point(1, -2)),
            Rock(Point(0, 0), Point(1, 0), Point(2, 0), Point(2, -1), Point(2, -2)),
            Rock(Point(0, -3), Point(0, -2), Point(0, -1), Point(0, 0)),
            Rock(Point(0, 0), Point(1, 0), Point(0, -1), Point(1, -1))
        )

        part1(expected = 3114) { input ->
            val jets = input.parseGasJets()
            var rock = rocks.first()
            var bottomLeft = startPoint
            var windIndex = 0
            val ground = floor.toMutableSet()
            var rockCount = 1
            while (rockCount < 2023) {
                val jetOffset = jets[windIndex++ % jets.size]

                val newX = (bottomLeft.x + jetOffset).coerceIn(0, columns - rock.width)
                val newPositionX = bottomLeft.copy(x = newX)
                if (rock.translated(newPositionX).none { it in ground }) {
                    bottomLeft = newPositionX
                }

                val newPosition = bottomLeft.copy(y = bottomLeft.y + 1)
                if (rock.translated(newPosition).none { it in ground }) {
                    bottomLeft = newPosition
                } else {
                    ground += rock.translated(bottomLeft)
                    rock = rocks[rockCount % rocks.size]
                    rockCount++
                    bottomLeft = startPoint + Point(0, ground.minOf { it.y })
                }
            }

            abs(ground.minOf { it.y.toFloat() }.toInt())
        }
        part1 test 1 expect 3068

        part2(expected = 1540804597682L) { input ->
            val jets = input.parseGasJets()
            var rock = rocks.first()
            var position = startPoint
            var jetIndex = 0

            val states = mutableMapOf<String, Int>()
            val heights = mutableListOf<Int>()
            val ground = floor.toMutableSet()
            var rockCount = 1
            var rockInitialJetIndex = 0
            while (true) {
                val jetOffset = jets[jetIndex++ % jets.size]

                val newX = (position.x + jetOffset).coerceIn(0, columns - rock.width)
                val newPositionX = position.copy(x = newX)
                if (rock.translated(newPositionX).none { it in ground }) {
                    position = newPositionX
                }

                val newPosition = position.copy(y = position.y + 1)
                if (rock.translated(newPosition).none { it in ground }) {
                    position = newPosition
                } else {
                    if (rockCount > 250) {
                        val key = key(rock, rockInitialJetIndex)
                        if (key in states) {
                            break
                        } else {
                            states += key to rockCount
                        }
                    }
                    ground += rock.translated(position)
                    val height = abs(ground.minOf { it.y.toFloat() }.toInt())
                    heights.add(height)
                    rock = rocks[rockCount % rocks.size]
                    position = startPoint + Point(0, ground.minOf { it.y })
                    rockCount++
                    rockInitialJetIndex = jetIndex % jets.size
                }
            }

            val key = key(rock, rockInitialJetIndex)
            val rockNumber = states.getValue(key)
            val heightBeforePattern = heights[rockNumber - 2]
            val heightWithPattern = heights.last()
            val heightPerPattern = heightWithPattern - heightBeforePattern

            val rocksLeftOnPatternStart = 1_000_000_000_000L - (rockNumber - 1)

            val countPerPattern = heights.drop(rockNumber - 1).count()
            val coveredByPattern = rocksLeftOnPatternStart / countPerPattern
            val after = rocksLeftOnPatternStart % countPerPattern

            val remaining = heights.drop(rockNumber - 2)
                .take(after.toInt() + 1).let { it.last() - it.first() }

            heightBeforePattern + coveredByPattern * heightPerPattern + remaining
        }
        part2 test 1 expect 1514285714288L
    }
}

private fun Input.parseGasJets() = lines.single().map { if (it == '<') -1 else 1 }

private fun key(shape: Rock, index: Int): String = "$shape $index"

private data class Rock(val points: List<Point>) {
    constructor(vararg points: Point) : this(points.toList())
    val width get() = points.maxOf { it.x } - points.minOf { it.x } + 1
    fun translated(position: Point): List<Point> = points.map { it + position } // should perhaps return a rock?
}
