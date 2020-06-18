package space.votebot.bot.command.impl

import mu.KotlinLogging
import space.votebot.bot.event.EventSubscriber
import space.votebot.bot.events.CommandErrorEvent
import space.votebot.bot.events.CommandExecutedEvent

/**
 * Command handler in debug mode.
 */
class DebugCommandHandler : AbstractCommandHandler() {

    private val logger = KotlinLogging.logger {}

    /**
     * Logs execution of commands.
     */
    @EventSubscriber
    fun commandExecuted(event: CommandExecutedEvent): Unit = logger.info { "${event.context.command.displayName} was executed by ${event.author.asTag}" }

    /**
     * Logs error during execution of commands.
     */
    @EventSubscriber
    fun commandErrored(event: CommandErrorEvent) {
        logger.error(event.error) { "An error occurred while executing a command in ${event.coroutineContext}." }
        event.context.respond("An Error occurred whilst executing command! See application log for more information")
                .queue()

    }
}