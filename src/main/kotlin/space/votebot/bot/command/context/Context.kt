package space.votebot.bot.command.context

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.restaction.MessageAction
import space.votebot.bot.command.AbstractCommand
import space.votebot.bot.command.CommandClient
import space.votebot.bot.constants.Embeds
import space.votebot.bot.util.Embeds
import space.votebot.bot.core.VoteBot
import space.votebot.bot.data.VoteBotUser

/**
 * Representation of a context of a command execution.
 * @property command the executed command
 * @param _args the [Arguments] of the command
 * @property commandClient the [CommandClient] which executed this command
 * @property bot instance of the [VoteBot]
 * @property message the message that triggered the command
 * @property responseNumber response number of triggering event
 * @property voteBotUser the votebotUse of the message
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class Context(
        bot: VoteBot,
        val command: AbstractCommand,
        val _args: Arguments?,
        message: Message,
        val commandClient: CommandClient,
        responseNumber: Long,
        voteBotUser: VoteBotUser
) : BaseContext(bot, message, responseNumber, voteBotUser) {

    /**
     * The [Arguments] of the command.
     */
    val args: Arguments
        get() = _args!!

    /**
     * Sends a help embed for [command].
     * @see Embeds.command
     */
    fun sendHelp(): MessageAction = respond(Embeds.command(command, this))

}
