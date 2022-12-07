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

    companion object {
        private const val PATH = "./src/main/resources/"

        private fun getInputFile(day: Int): File =
            File("$PATH/input-day-$day.txt")

        private fun getTestFile(day: Int, n: Int): File =
            File("$PATH/tests/test-day-$day-$n.txt")
    }
}

fun Input.toInts() = lines.toInts()
fun Input.toLongs() = lines.toLongs()
fun Input.single() = lines.single()
fun Input.slicedByBlank() = lines.sliceByBlank()
