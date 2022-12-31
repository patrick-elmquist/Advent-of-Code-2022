package common

import common.util.Point
import java.io.File

data class Input(val lines: List<String>) {
    constructor(file: File) : this(file.readLines())
    constructor(day: Int) : this(inputFileFor(day = day))
    constructor(day: Int, test: Int) : this(testFileFor(day = day, n = test))

    companion object
}

val Input.pointCharMap: Map<Point, Char>
    get() = lines.flatMapIndexed { y, row -> row.mapIndexed { x, c -> Point(x, y) to c } }.toMap()
