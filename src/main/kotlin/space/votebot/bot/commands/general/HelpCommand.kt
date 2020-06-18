package space.votebot.bot.commands.general

import space.votebot.bot.command.AbstractCommand
import space.votebot.bot.command.CommandCategory
import space.votebot.bot.command.context.Context
import space.votebot.bot.command.permission.Permission
import space.votebot.bot.constants.Embeds

/**
 * Help command.
 */
class HelpCommand : AbstractCommand() {
    override val aliases: List<String> = listOf("help", "h")
    override val displayName: String = "help"
    override val usage: String = "[command]"
    override val exampleUsage: String = "help"
    override val permission: Permission = Permission.ANY
    override val category: CommandCategory = CommandCategory.GENERAL

    override suspend fun execute(context: Context) {
        val commandName = context.args.optionalArgument(0)
        if (commandName == null) {
            sendCommandList(context)
        } else {
            sendCommandHelpMessage(context, commandName)
        }
    }

    private fun sendCommandHelpMessage(context: Context, commandName: String) {
        val translations = context.translations
        val command = context.commandClient.commandAssociations[commandName.toLowerCase()]
                ?: return context.respond(
                        Embeds.error(
                                translations.t("commands.help.not_found.title"),
                                translations.t("commands.help.not_found.description")
                        )
                ).queue()

        context.respond(Embeds.command(command, context)).queue()
    }

    private fun sendCommandList(context: Context) {
        context.respond(
                Embeds.info(context.translations.t("commands.help.info.title"), context.translations.t("commands.help.info.description")) {
                    val commands = context.commandClient.registeredCommands.filter {
                        context.commandClient.permissionHandler.isCovered(
                                it,
                                context.member
                        )
                    }
                    CommandCategory.values().forEach { category ->
                        val categoryCommands = commands.filter { it.category == category }.map { it.name }
                        if (categoryCommands.isNotEmpty()) {
                            addField(
                                    category.displayName,
                                    categoryCommands.joinToString(prefix = "`", separator = "`, `", postfix = "`")
                            )
                        }
                    }
                }
        ).queue()
    }
}
