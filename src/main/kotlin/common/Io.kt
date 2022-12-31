package common

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

private const val PATH_RESOURCES = "./src/main/kotlin/day%s/"
private const val URL_FORMAT_INPUT = "https://adventofcode.com/2022/day/%d/input"
private const val FILENAME_FORMAT_INPUT = "input-day-%s.txt"
private const val FILENAME_FORMAT_TEST = "test-day-%s-%d.txt"
private const val FILENAME_PROPERTIES = "local.properties"
private const val PROPERTY_KEY_SESSION = "session"

fun inputFileFor(day: Int): File =
    File("${PATH_RESOURCES.format(day.zeroPad())}/${FILENAME_FORMAT_INPUT.format(day.zeroPad())}")

fun testFileFor(day: Int, n: Int): File =
    File("${PATH_RESOURCES.format(day.zeroPad())}/${FILENAME_FORMAT_TEST.format(day.zeroPad(), n)}")

suspend fun makeSureInputFileIsAvailable(day: Int): Boolean {
    val filename = FILENAME_FORMAT_INPUT.format(day.zeroPad())
    val inputFile = File("${PATH_RESOURCES.format(day.zeroPad())}/$filename")
    if (inputFile.isNonEmptyFile) {
        return true
    } else {
        println("Fetching $filename")
    }
    val session = readSessionFromProperties()
    if (session == null) {
        println("Could not find session data")
        return false
    }
    val client = HttpClient(Java) {
        install(Logging) {
            level = LogLevel.NONE
        }
    }
    val response = client.get(URL_FORMAT_INPUT.format(day)) {
        header("Cookie", "session=$session")
    }
    if (response.status == HttpStatusCode.NotFound) {
        println("404 - Challenge input is not available yet")
        // Creating a dummy file to be able to run the prepared day when empty
        withContext(Dispatchers.IO) {
            inputFile.createNewFile()
        }
        return true
    }
    val responseBody = response.body<ByteArray>()
    inputFile.writeBytes(responseBody)
    return true
}

private fun readSessionFromProperties(): String? {
    val propertiesFile = File(FILENAME_PROPERTIES)
    return if (propertiesFile.isNonEmptyFile) {
        propertiesFile.inputStream().use { reader ->
            Properties().apply { load(reader) }.getProperty(PROPERTY_KEY_SESSION)
        }
    } else {
        println("Could not find local.properties file")
        null
    }
}

private val File.isNonEmptyFile: Boolean
    get() = with(this) { exists() && isFile && length() > 0L }

private fun Int.zeroPad(length: Int = 2) = toString().padStart(length = length, padChar = '0')

