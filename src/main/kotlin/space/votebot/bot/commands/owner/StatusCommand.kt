package space.votebot.bot.commands.owner

import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import space.votebot.bot.command.AbstractCommand
import space.votebot.bot.command.CommandCategory
import space.votebot.bot.command.context.Context
import space.votebot.bot.command.permission.Permission
import space.votebot.bot.constants.Embeds
import space.votebot.bot.core.AnimatedGame
import java.time.Duration

class StatusCommand : AbstractCommand() {
    override val aliases: List<String> = listOf("status")
    override val displayName: String = "Status"
    override val usage: String = "[type] [status] [duration] <game>"
    override val permission: Permission = Permission.BOT_OWNER
    override val category: CommandCategory = CommandCategory.BOT_OWNER

    override suspend fun execute(context: Context) {
        val args = context.args
        val game = when (args.size) {
            0 -> return context.sendHelp().queue()
            1 -> AnimatedGame(text = args.join())
            2 -> {
                val type = args.enum<Activity.ActivityType>(0, context) ?: return
                AnimatedGame(type = type, text = args.subList(1, args.size).joinToString(" "))
            }
            3 -> {
                val type = args.enum<Activity.ActivityType>(0, context) ?: return
                val status = args.enum<OnlineStatus>(1, context) ?: return
                AnimatedGame(type = type, status = status, text = args.subList(2, args.size).joinToString(" "))
            }
            else -> {
                val type = args.enum<Activity.ActivityType>(0, context) ?: return
                val status = args.enum<OnlineStatus>(1, context) ?: return
                val duration = args.long(2, context) ?: return
                AnimatedGame(type = type, status = status, duration = Duration.ofSeconds(duration), text = args.subList(2, args.size).joinToString(" "))
            }
        }

        context.bot.gameAnimator.push(game)
        context.respond(Embeds.success("Successfully pushed game!", "You game has been added to the game animator queue.")).queue()
    }
}
