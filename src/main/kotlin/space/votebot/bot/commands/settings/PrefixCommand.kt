package space.votebot.bot.commands.settings

import com.i18next.java.Operation
import org.jetbrains.exposed.sql.transactions.transaction
import space.votebot.bot.command.AbstractCommand
import space.votebot.bot.command.AbstractSubCommand
import space.votebot.bot.command.CommandCategory
import space.votebot.bot.command.context.Context
import space.votebot.bot.command.permission.Permission
import space.votebot.bot.config.Config
import space.votebot.bot.constants.Constants
import space.votebot.bot.constants.Embeds
import space.votebot.bot.database.VoteBotGuild

class PrefixCommand : AbstractCommand() {
    override val aliases: List<String> = listOf("prefix", "p")
    override val displayName: String = "prefix"
    override val usage: String = "<prefix>"
    override val exampleUsage: String = "v?"
    override val permission: Permission = Permission.ADMIN
    override val category: CommandCategory = CommandCategory.SETTINGS

    init {
        registerCommands(
                ResetDefaultPrefixCommand(),
                ToggleDefaultPrefixCommand()
        )
    }

    override suspend fun execute(context: Context) {
        val translations = context.translations
        val prefix = context.args.requiredArgument(0, context) ?: return
        if (prefix.length > 5) {
            context.respond(Embeds.error(translations.t("commands.prefix.too_long.title"), translations.t("commands.prefix.too_long.description"))).queue()
            return
        }
        transaction {
            val guild = VoteBotGuild.findByGuildIdOrNew(context.guild.idLong)
            guild.prefix = prefix
        }
        context.respond(Embeds.success(translations.t("commands.prefix.prefix_updated.title"), translations.t("commands.prefix.too_long.description", Operation.Interpolation("newPrefix", prefix)))).queue()
    }

    private inner class ResetDefaultPrefixCommand : AbstractSubCommand(this) {
        override val aliases: List<String> = listOf("reset")
        override val displayName: String = "reset"
        override val description: String = "Resets the prefix to the default. (`${Constants.prefix}`)"
        override val usage: String = ""

        override suspend fun execute(context: Context) {
            val translations = context.translations
            transaction {
                val guild = VoteBotGuild.findByGuildIdOrNew(context.guild.idLong)
                guild.prefix = Constants.prefix
                guild.disableDefaultPrefix = false
            }
            context.respond(Embeds.success(translations.t("commands.prefix.reset.success.title"), translations.t("commands.prefix.reset.success.description", Operation.Interpolation("prefix", Config.defaultPrefix)))).queue()
        }
    }

    private inner class ToggleDefaultPrefixCommand : AbstractSubCommand(this) {
        override val aliases: List<String> = listOf("toggle-default")
        override val displayName: String = "toggle-default"
        override val description: String = "Toggles the default (`${Constants.prefix}`) prefix. (Only works if you have set a custom prefix)"
        override val usage: String = ""
        override suspend fun execute(context: Context) {
            val translations = context.translations
            val guild = transaction { VoteBotGuild.findByGuildIdOrNew(context.guild.idLong) }
            if (guild.prefix == Constants.prefix && !guild.disableDefaultPrefix) {
                context.respond(Embeds.error(translations.t("commands.prefix.toggle.not_allowed.title"), translations.t("commands.prefix.toggle.not_allowed.title"))).queue()
                return
            }
            transaction {
                guild.disableDefaultPrefix = !guild.disableDefaultPrefix
            }
            context.respond(Embeds.success("Toggled default prefix!", "You ${if (guild.disableDefaultPrefix) "enabled" else "disabled"} the default prefix. (`${Constants.prefix}`)")).queue()
        }
    }
}