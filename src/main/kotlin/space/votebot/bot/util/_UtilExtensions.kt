package space.votebot.bot.util

import kotlinx.coroutines.future.await
import mu.KotlinLogging
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.utils.data.DataObject
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import space.votebot.bot.command.AbstractCommand
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Duration
import java.time.temporal.TemporalUnit
import java.util.concurrent.CompletableFuture

private val httpLogger = KotlinLogging.logger("HttpClient")

/**
 * Checks whether a string is numeric or not.
 */
fun String.isNumeric(): Boolean = all(Char::isDigit)

/**
 * Checks whether a string is not numeric or not
 * @see isNumeric
 */
@Suppress("unused")
fun String.isNotNumeric(): Boolean = !isNumeric()

/**
 * Checks whether a command has subcommands or not.
 */
fun AbstractCommand.hasSubCommands(): Boolean = commandAssociations.isNotEmpty()

/**
 * @see net.dv8tion.jda.api.entities.IMentionable.getAsMention
 */
fun Member.asMention(): String = "<@!$id>"

/**
 * Executes a [Call] asynchronously.
 * @see Call.enqueue
 * @return a [CompletableFuture] containing the [Response]
 */
fun Call.executeAsync(): CompletableFuture<Response> {
    val future = CompletableFuture<Response>().exceptionally {
        httpLogger.error(it) { "An error ocurred while executing an HTTP request" }
        null
    }
    enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            future.completeExceptionally(e)
        }

        override fun onResponse(call: Call, response: Response) {
            future.complete(response)
        }

    })
    return future
}

/**
 * Returns the Stacktrace as a String.
 */
fun Throwable.stringify(): String {
    val stringWriter = StringWriter()
    val printWriter = PrintWriter(stringWriter)
    return stringWriter.use {
        printWriter.use {
            printStackTrace(printWriter)
            stringWriter.buffer.toString()
        }
    }
}

/**
 * Limits the length of a string by [amount] and adds [contraction] at the end.
 */
fun String.limit(amount: Int, contraction: String = "..."): String =
        if (length < amount) this else "${substring(0, amount - contraction.length)}$contraction"

/**
 * Public map constructor of [DataObject].
 */
class MapJsonObject(map: Map<String, Any>) : DataObject(map)

/**
 * Awaits the completion of a rest action
 */
suspend fun <T> RestAction<T>.await(): T = submit().await()

/**
 * Sends a message and deletes it after a [delay].
 *
 * @see Message.delete
 */
fun RestAction<Message>.delete(delay: Duration) = delay(delay).flatMap(Message::delete).queue()

/**
 * Sends a message and deletes it after a [time] in [unit].
 *
 * @see delete
 */
fun RestAction<Message>.delete(time: Long, unit: TemporalUnit) = delete(Duration.of(time, unit))
