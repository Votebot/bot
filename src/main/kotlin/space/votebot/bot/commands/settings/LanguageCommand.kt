package space.votebot.bot.commands.settings

import org.jetbrains.exposed.sql.transactions.transaction
import space.votebot.bot.command.AbstractCommand
import space.votebot.bot.command.CommandCategory
import space.votebot.bot.command.context.Context
import space.votebot.bot.command.permission.Permission
import space.votebot.bot.constants.Embeds

class LanguageCommand : AbstractCommand() {
    override val aliases: List<String> = listOf("language", "lang")
    override val displayName: String = "language"
    override val usage: String = "[language]"
    override val permission: Permission = Permission.ANY
    override val category: CommandCategory = CommandCategory.SETTINGS

    override suspend fun execute(context: Context) {
        val language = context.args.requiredArgument(0, context) ?: return
        transaction {
            val user = context.voteBotUser
            user.locale = language
        }
        context.respond(Embeds.success(
                "Changed",
                "to $language"
        )).queue()
    }
}