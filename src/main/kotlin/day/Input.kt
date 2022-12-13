package day

import util.sliceByBlank
import util.toInts
import util.toLongs
import java.io.File

data class Input(val lines: List<String>) {
    constructor(string: String) : this(string.split("\n"))
    constructor(day: Int) : this(getInputFile(day = day))
    constructor(day: Int, test: Int) : this(getTestFile(day = day, n = test))
    constructor(file: File) : this(file.readLines())

    companion object
}

fun Input.toInts() = lines.toInts()
fun Input.toLongs() = lines.toLongs()
fun Input.single() = lines.single()
fun Input.slicedByBlank() = lines.sliceByBlank()
