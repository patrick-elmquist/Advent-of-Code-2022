package util

typealias Matrix<T> = List<List<T>>

typealias MutableMatrix<T> = List<MutableList<T>>

fun <T : Any> Array<Array<T>>.print() =
    forEach { row ->
        row.forEach { pixel -> print(pixel) }
        println()
    }
