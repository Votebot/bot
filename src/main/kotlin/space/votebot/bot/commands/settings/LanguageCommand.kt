package space.votebot.bot.commands.settings

import com.i18next.java.Operation
import org.jetbrains.exposed.sql.transactions.transaction
import space.votebot.bot.command.AbstractCommand
import space.votebot.bot.command.CommandCategory
import space.votebot.bot.command.context.Context
import space.votebot.bot.command.permission.Permission
import space.votebot.bot.command.translation.TranslationManager
import space.votebot.bot.constants.Embeds

class LanguageCommand : AbstractCommand() {
    override val aliases: List<String> = listOf("language", "lang")
    override val displayName: String = "language"
    override val usage: String = "[language]"
    override val permission: Permission = Permission.ANY
    override val category: CommandCategory = CommandCategory.SETTINGS

    override suspend fun execute(context: Context) {
        val language = context.args.requiredArgument(0, context) ?: return
        val translations = context.translations
        transaction {
            val user = context.voteBotUser
            user.locale = language
        }

        if (language !in TranslationManager.supportedLanguages) {
            return context.respond(Embeds.error(
                    translations.t("commands.language.unknown.title"),
                    translations.t("commands.language.unknown.description", Operation.Interpolation(mapOf("language" to language, "supportedLanguages" to TranslationManager.supportedLanguages.joinToString(prefix = "`", separator = "`, `", postfix = "`"))))
            )).queue()
        }

        context.respond(Embeds.success(
                translations.t("commands.language.success.title"),
                translations.t("commands.language.success.description", Operation.Interpolation(mapOf("language" to language, "languageName" to translations.t("name"))))
        )).queue()
    }
}