package space.votebot.bot.command.impl

import mu.KotlinLogging
import space.votebot.bot.event.EventSubscriber
import space.votebot.bot.events.CommandErrorEvent
import space.votebot.bot.events.CommandExecutedEvent

class DebugCommandHandler {

    private val logger = KotlinLogging.logger {}

    @EventSubscriber
    fun commandExecuted(event: CommandExecutedEvent) = logger.info { "${event.context.command.displayName} was executed by ${event.author.asTag}" }

    @EventSubscriber
    fun commandErrored(event: CommandErrorEvent) {
        logger.error(event.error) { "An error occurred while executing a command in ${event.coroutineContext}." }
        event.context.respond("An Error occurred whilst executing command! See application log for more information")
                .queue()

    }
}