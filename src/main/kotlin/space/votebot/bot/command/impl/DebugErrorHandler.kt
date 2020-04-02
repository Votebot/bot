package space.votebot.bot.command.impl

import mu.KotlinLogging
import space.votebot.bot.command.ErrorHandler
import space.votebot.bot.command.context.Context
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [ErrorHandler] that only logs the error.
 */
class DebugErrorHandler : ErrorHandler {

    private val logger = KotlinLogging.logger { }

    override fun handleException(
        exception: Throwable,
        context: Context,
        thread: Thread,
        coroutineContext: CoroutineContext?
    ) {
        logger.error(exception) { "An error occurred while executing a command in $context." }
        context.respond("An error occurred.")
            .queue()
    }
}