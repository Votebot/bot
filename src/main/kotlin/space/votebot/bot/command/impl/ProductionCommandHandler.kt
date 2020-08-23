package space.votebot.bot.command.impl

import mu.KotlinLogging
import space.votebot.bot.command.context.Context
import space.votebot.bot.constants.Embeds
import space.votebot.bot.constants.Emotes
import space.votebot.bot.dsl.editMessage
import space.votebot.bot.dsl.sendMessage
import space.votebot.bot.event.EventSubscriber
import space.votebot.bot.events.CommandErrorEvent
import space.votebot.bot.util.HastebinUtil
import space.votebot.bot.util.stringify
import java.time.LocalDateTime
import kotlin.coroutines.CoroutineContext

/**
 * Command handler used in production.
 */
class ProductionCommandHandler(private val errorReportChannel: Long) : AbstractCommandHandler() {

    private val logger = KotlinLogging.logger {}

    /**
     * Reports error to hastebin.
     */
    @EventSubscriber
    fun commandErrored(event: CommandErrorEvent) {
        logger.error(event.error) { "An error occurred whilst command execution" }
        event.context.respond(
                Embeds.error(
                        "An error occurred!",
                        "${Emotes.LOADING} I am collecting information about this error. Please wait."
                )
        ).submit().thenCompose { message ->
            val error = collectErrorInformation(event.error, event.context, event.thread, event.coroutineContext)
            HastebinUtil.postErrorToHastebin(error, event.context.jda.httpClient).thenApply { it to message }
        }.thenAccept { (url, message) ->
            event.jda.getTextChannelById(errorReportChannel)?.sendMessage(Embeds.error("New error report!", url))?.queue()
            message.editMessage(
                    Embeds.error(
                            "Error Report Created!",
                            "An error report was created. My developers will now investigate it and implement a fix."
                    )
            ).queue()
        }
    }

    private fun collectErrorInformation(
            e: Throwable,
            context: Context,
            thread: Thread,
            coroutineContext: CoroutineContext?
    ): String {
        val information = StringBuilder()
        val channel = context.channel
        information.append("TextChannel: ").append('#').append(channel.name)
                .append('(').append(channel.id).appendLine(")")
        val guild = context.guild
        information.append("Guild: ").append(guild.name).append('(').append(guild.id)
                .appendLine(')')
        val executor = context.author
        information.append("Executor: ").append('@').append(executor.name).append('#')
                .append(executor.discriminator).append('(').append(executor.id).appendLine(')')
        val selfMember = guild.selfMember
        information.append("Permissions: ").appendLine(selfMember.permissions)
        information.append("Channel permissions: ").appendLine(selfMember.getPermissions(channel))
        information.append("Timestamp: ").appendLine(LocalDateTime.now())
        information.append("Thread: ").appendLine(thread)
        information.append("Coroutine: ").appendLine(coroutineContext)
        information.append("Stacktrace: ").appendLine().append(e.stringify())
        return information.toString()
    }
}
