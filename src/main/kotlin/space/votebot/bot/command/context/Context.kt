

package space.votebot.bot.command.context

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.requests.restaction.MessageAction
import space.votebot.bot.command.AbstractCommand
import space.votebot.bot.command.CommandClient
import space.votebot.bot.command.permission.Permission
import space.votebot.bot.constants.Embeds
import space.votebot.bot.core.Discord
import space.votebot.bot.core.VoteBot
import space.votebot.bot.dsl.EmbedConvention
import space.votebot.bot.dsl.sendMessage

/**
 * Representation of a context of a command execution.
 * @property command the executed command
 * @property args the [Arguments] of the command
 * @property commandClient the [CommandClient] which executed this command
 * @property bot instance of the [VoteBot]
 * @property message the message that triggered the command
 * @property responseNumber response number of triggering event
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
data class Context(
        val bot: VoteBot,
        val command: AbstractCommand,
        val args: Arguments,
        val message: Message,
        val commandClient: CommandClient,
        val responseNumber: Long
) {

    /**
     * The [Discord] instance.
     */
    val discord: Discord
        get() = bot.discord

    /**
     * The [JDA] instance.
     */
    val jda: JDA
        get() = message.jda

    /**
     * The id of [message].
     */
    val messageId: Long
        get() = message.idLong

    /**
     * The [TextChannel] of [message].
     */
    val channel: TextChannel
        get() = message.textChannel

    /**
     * The author of the [message].
     */
    val author: User
        get() = message.author

    /**
     * The member of the [author].
     */
    val member: Member
        get() = message.member!! //CommandClient ignores webhook messages, so this cannot be null

    /**
     * The guild of the [channel].
     */
    val guild: Guild
        get() = message.guild

    /**
     * The [self member][Member] of the bot.
     */
    val me: Member
        get() = guild.selfMember

    /**
     * The [SelfUser] of the bot.
     */
    val selfUser: SelfUser
        get() = jda.selfUser

    /**
     * Sends [content] into [channel].
     * @return a [MessageAction] that sends the message
     */
    fun respond(content: String): MessageAction = channel.sendMessage(content)

    /**
     * Sends [embed] into [channel].
     * @return a [MessageAction] that sends the message
     */
    fun respond(embed: MessageEmbed): MessageAction = channel.sendMessage(embed)

    /**
     * Sends [embedBuilder] into [channel].
     * @return a [MessageAction] that sends the message
     */
    fun respond(embedBuilder: EmbedBuilder): MessageAction = channel.sendMessage(embedBuilder.build())

    /**
     * Sends [embed] into [channel].
     * @return a [MessageAction] that sends the message
     */
    fun respond(embed: EmbedConvention): MessageAction = channel.sendMessage(embed)

    /**
     * Sends a help embed for [command].
     * @see Embeds.command
     */
    fun sendHelp(): MessageAction = respond(Embeds.command(command))

    /**
     * Checks whether the [member] has [Permission.ADMIN] or not.
     */
    fun hasAdmin(): Boolean = hasPermission(Permission.ADMIN)

    private fun hasPermission(permission: Permission) = commandClient.permissionHandler.isCovered(permission, member)

}
