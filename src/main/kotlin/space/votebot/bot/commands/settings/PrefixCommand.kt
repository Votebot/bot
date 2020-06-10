package space.votebot.bot.commands.settings

import org.jetbrains.exposed.sql.transactions.transaction
import space.votebot.bot.command.AbstractCommand
import space.votebot.bot.command.AbstractSubCommand
import space.votebot.bot.command.CommandCategory
import space.votebot.bot.command.context.Context
import space.votebot.bot.command.permission.Permission
import space.votebot.bot.constants.Constants
import space.votebot.bot.constants.Embeds
import space.votebot.bot.database.VoteBotGuild

class PrefixCommand : AbstractCommand() {
    override val aliases: List<String> = listOf("prefix", "p")
    override val displayName: String = "prefix"
    override val description: String = "Configure the bot's prefix."
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
        val prefix = context.args.requiredArgument(0, context) ?: return
        if (prefix.length > 5) {
            context.respond(Embeds.error("Prefix too long!", "The prefix can be a maximum of 5 characters long.")).queue()
            return
        }
        transaction {
            val guild = VoteBotGuild.findByGuildIdOrNew(context.guild.idLong)
            guild.prefix = prefix
        }
        context.respond(Embeds.success("Prefix updated!", "Updated prefix to `${prefix}`")).queue()
    }

    private inner class ResetDefaultPrefixCommand : AbstractSubCommand(this) {
        override val aliases: List<String> = listOf("reset")
        override val displayName: String = "reset"
        override val description: String = "Resets the prefix to the default. (`${Constants.prefix}`)"
        override val usage: String = ""

        override suspend fun execute(context: Context) {
            transaction {
                val guild = VoteBotGuild.findByGuildIdOrNew(context.guild.idLong)
                guild.prefix = Constants.prefix
                guild.disableDefaultPrefix = false
            }
            context.respond(Embeds.success("Prefix reset!", "Prefix is now again `${Constants.prefix}`")).queue()
        }
    }

    private inner class ToggleDefaultPrefixCommand : AbstractSubCommand(this) {
        override val aliases: List<String> = listOf("toggle-default")
        override val displayName: String = "toggle-default"
        override val description: String = "Toggles the default (`${Constants.prefix}`) prefix. (Only works if you have set a custom prefix)"
        override val usage: String = ""
        override suspend fun execute(context: Context) {
            val guild = transaction { VoteBotGuild.findByGuildIdOrNew(context.guild.idLong) }
            if (guild.prefix == Constants.prefix && !guild.disableDefaultPrefix) {
                context.respond(Embeds.error("Not allowed to disable!", "You first have to set a custom prefix in order to disable the default prefix.")).queue()
                return
            }
            transaction {
                guild.disableDefaultPrefix = !guild.disableDefaultPrefix
            }
            context.respond(Embeds.success("Toggled default prefix!", "You ${if (guild.disableDefaultPrefix) "enabled" else "disabled"} the default prefix. (`${Constants.prefix}`)")).queue()
        }
    }
}