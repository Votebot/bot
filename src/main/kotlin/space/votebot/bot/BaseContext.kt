package space.votebot.bot

import com.i18next.java.I18Next
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.requests.restaction.MessageAction
import org.jetbrains.exposed.sql.transactions.transaction
import space.votebot.bot.command.translation.TranslationManager
import space.votebot.bot.core.Discord
import space.votebot.bot.core.VoteBot
import space.votebot.bot.database.VoteBotUser
import space.votebot.bot.dsl.EmbedConvention
import space.votebot.bot.dsl.sendMessage

/**
 * Representation of a context of a command execution.
 * @property bot instance of the [VoteBot]
 * @property message the message that triggered the command
 * @property responseNumber response number of triggering event
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class BaseContext(
        val bot: VoteBot,
        val message: Message,
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
     * [VoteBotUser] of the
     */
    val voteBotUser: VoteBotUser by lazy {
        transaction { VoteBotUser.findById(author.idLong) ?: VoteBotUser.new(author.idLong) {} }
    }

    /**
     * Translation based on [voteBotUser].
     */
    val translations: I18Next by lazy { TranslationManager.forUser(voteBotUser) }

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

}
