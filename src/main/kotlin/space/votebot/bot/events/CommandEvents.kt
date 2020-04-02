package space.votebot.bot.events

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import space.votebot.bot.command.context.Context
import space.votebot.bot.command.permission.Permission
import space.votebot.bot.core.VoteBot
import space.votebot.bot.core.VoteBotImpl
import space.votebot.bot.event.EventDescriber
import javax.annotation.Nonnull
import kotlin.coroutines.CoroutineContext

/**
 * Generic command event.
 * @see GuildMessageReceivedEvent
 * @property context the [Context] in which the command was executed
 */
@EventDescriber(callParents = false)
sealed class CommandEvent(api: @Nonnull JDA, responseNumber: Long, message: @Nonnull Message, val context: Context) : GuildMessageReceivedEvent(api, responseNumber, message) {

    /**
     * The [VoteBotImpl] instance.
     */
    val bot: VoteBot
        get() = context.bot
}

/**
 * Event that is fired when a command was executed successfully.
 */
class CommandExecutedEvent(api: @Nonnull JDA, responseNumber: Long, message: @Nonnull Message, context: Context) : CommandEvent(api, responseNumber, message, context)

/**
 * Event that is fired when an error occurred during command execution.
 * @property error the [Throwable] that was triggering the event
 * @property coroutineContext the [CoroutineContext] in which the command was executed
 * @property thread the thread in which the command was executed
 */
class CommandErrorEvent(val error: Throwable, val coroutineContext: CoroutineContext, val thread: Thread, api: @Nonnull JDA, responseNumber: Long, message: @Nonnull Message, context: Context) : CommandEvent(api, responseNumber, message, context)

/**
 * Event that is fired when a user does not have the required permission to execute a command
 */
class CommandNoPermissionEvent(api: @Nonnull JDA, responseNumber: Long, message: @Nonnull Message, context: Context) : CommandEvent(api, responseNumber, message, context) {

    /**
     * The permission that is needed to execute the command
     */
    val permission: Permission
        get() = context.command.permission
}