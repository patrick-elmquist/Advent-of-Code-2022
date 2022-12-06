package day

import util.groupByBlank
import util.toInts
import util.toLongs
import java.io.File

data class Input(val lines: List<String>) {
    constructor(string: String) : this(string.split("\n"))
    constructor(day: Int) : this(getInputFile(day = day))
    constructor(day: Int, test: Int) : this(getTestFile(day = day, n = test))
    constructor(file: File) : this(file.readLines())

    companion object {
        private const val RESOURCES_PATH = "./src/main/resources/"

        private fun getInputFile(day: Int): File =
            File("$RESOURCES_PATH/input-day-$day.txt")

        private fun getTestFile(day: Int, n: Int): File =
            File("$RESOURCES_PATH/tests/test-day-$day-$n.txt")
    }
}

fun Input.toInts() = lines.toInts()
fun Input.toLongs() = lines.toLongs()
fun Input.single() = lines.single()
fun Input.groupByBlank() = lines.groupByBlank()
