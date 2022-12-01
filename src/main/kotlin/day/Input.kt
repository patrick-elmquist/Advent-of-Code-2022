package day

import util.groupByBlank
import util.toInts
import util.toLongs
import java.io.File

data class Input(val lines: List<String>) {
    constructor(string: String) : this(string.split("\n"))
    constructor(day: Int) : this(File("./assets/input-day-$day.txt"))
    constructor(file: File) : this(file.readLines())
}

fun Input.toInts() = lines.toInts()
fun Input.toLongs() = lines.toLongs()
fun Input.single() = lines.single()
fun Input.groupByBlank() = lines.groupByBlank()
