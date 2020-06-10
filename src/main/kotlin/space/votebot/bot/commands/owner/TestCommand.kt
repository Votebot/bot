package space.votebot.bot.commands.owner

import space.votebot.bot.command.AbstractCommand
import space.votebot.bot.command.AbstractSubCommand
import space.votebot.bot.command.CommandCategory
import space.votebot.bot.command.context.Context
import space.votebot.bot.command.permission.Permission

class TestCommand : AbstractCommand() {

    override val aliases: List<String> = listOf("test")

    override val displayName: String = "Test"
    override val description: String = "Secret command for cool people"
    override val usage: String = ""
    override val permission: Permission = Permission.BOT_OWNER
    override val category: CommandCategory = CommandCategory.BOT_OWNER
    override suspend fun execute(context: Context) = context.sendHelp().queue()

    init {
        registerCommands(PaginatorCommand())
    }


    private inner class PaginatorCommand : AbstractSubCommand(this) {
        override val aliases: List<String> = listOf("list", "paginator")
        override val displayName: String = "List"
        override val description: String = "Test for paginator"
        override val usage: String = "<items>"

        override suspend fun execute(context: Context) {
//            Paginator.create(
//                    context.jda.eventManager,
//                    listOf(context.member),
//                    items = context.args,
//                    channel = context.channel,
//                    title = "The list",
//                    loadingTitle = "Loading ...",
//                    loadingDescription = "Just wait a moment"
//            )
        }
    }
}
