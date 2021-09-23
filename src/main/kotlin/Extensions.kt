import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.UnsupportedEncodingException
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/** Send an http request to the specified [uri] and return the body with [bodyHandler]. */
suspend fun <T> HttpClient.load(uri: URI, bodyHandler: HttpResponse.BodyHandler<T>): T = withContext(Dispatchers.IO) {
    @Suppress("BlockingMethodInNonBlockingContext")
    send(httpRequest { uri(uri) }, bodyHandler).body()
}

/** Builder function [HttpRequest]. */
inline fun httpRequest(block: HttpRequest.Builder.() -> Unit = {}): HttpRequest =
    HttpRequest.newBuilder().apply(block).build()

/**
 * Translates a string into application/x-www-form-urlencoded format using UTF-8 scheme.
 * @throws UnsupportedEncodingException If the named encoding is not supported.
 * @return the translated String.
 */
@Throws(UnsupportedEncodingException::class)
fun String.toXWwwFormUrl(): String =
    URLEncoder.encode(this, "UTF-8")

/**
 * Creates a URI by parsing the given string.
 * @throws IllegalArgumentException  If the given string violates RFC 2396.
 * @return The new URI.
 */
@Throws(IllegalArgumentException::class)
fun String.toURI(): URI = URI.create(this)

/** Do [action] with delay after each action. */
suspend inline fun <T> Iterable<T>.executeWithDelay(action: (T) -> Unit, delayTime: (index: Int, word: T) -> Long?) {
    for ((index, elem) in withIndex()) {
        action(elem)
        delayTime(index, elem)?.let { delay(it) }
    }
}

