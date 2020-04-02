package space.votebot.bot.command

import space.votebot.bot.command.permission.Permission

/**
 * Skeleton of a sub command.
 * @property parent the parent of the command
 * @property callback an [Exception] that is supposed to highlight class defention line
 * @see AbstractCommand
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class AbstractSubCommand(val parent: AbstractCommand) : AbstractCommand() {
    override val callback: Exception = Exception()
    override val category: CommandCategory
        get() = parent.category
    override val permission: Permission
        get() = parent.permission
}
