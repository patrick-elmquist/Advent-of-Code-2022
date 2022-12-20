package day

import java.io.File

data class Input(val lines: List<String>) {
    constructor(file: File) : this(file.readLines())
    constructor(day: Int) : this(inputFileFor(day = day))
    constructor(day: Int, test: Int) : this(testFileFor(day = day, n = test))
    companion object
}
