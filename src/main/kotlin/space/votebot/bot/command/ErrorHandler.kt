package space.votebot.bot.command

import space.votebot.bot.command.context.Context
import kotlin.coroutines.CoroutineContext

/**
 * Interface for handling errors during command execution
 */
interface ErrorHandler {

    /**
     * Handles the [exception] in [context].
     */
    fun handleException(
        exception: Throwable,
        context: Context,
        thread: Thread,
        coroutineContext: CoroutineContext? = null
    )
}
