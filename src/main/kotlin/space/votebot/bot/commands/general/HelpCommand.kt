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
    override val description: String = "Shows a list containing all available commands."
    override val usage: String = "[command]"
    override val permission: Permission = Permission.ANY
    override val category: CommandCategory = CommandCategory.GENERAL

    override fun execute(context: Context) {
        val commandName = context.args.optionalArgument(0)
        if (commandName == null) {
            sendCommandList(context)
        } else {
            sendCommandHelpMessage(context, commandName)
        }
    }

    private fun sendCommandHelpMessage(context: Context, commandName: String) {
        val command = context.commandClient.commandAssociations[commandName.toLowerCase()]
                ?: return context.respond(
                        Embeds.error(
                                "Command not found!",
                                "I couldn't find a command with that name"
                        )
                ).queue()

        context.respond(Embeds.command(command)).queue()
    }

    private fun sendCommandList(context: Context) {
        context.respond(
                Embeds.info(
                        "Command-Help", """This is a list of all commands,
            | in order to learn more about a specific command run `v!help [command]`
        """.trimMargin()
                ) {
                    val commands = context.commandClient.registeredCommands.filter {
                        context.commandClient.permissionHandler.isCovered(
                                it.permission,
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
