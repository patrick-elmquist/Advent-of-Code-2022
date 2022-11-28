package day

import util.toInts
import util.toLongs
import java.io.File

data class Input(val lines: List<String>) {
    val ints by lazy { lines.toInts() }
    val longs by lazy { lines.toLongs() }
    val single by lazy { lines.single() }

    constructor(day: Int) : this(File("./assets/input-day-$day.txt"))
    constructor(file: File) : this(file.readLines())
}
