package space.votebot.bot.command

import space.votebot.bot.command.context.Context
import space.votebot.bot.command.permission.Permission

/**
 * Skeleton of a command.
 * @property aliases list of strings that invoke the command
 * @property name the name used in usage messages
 * @property displayName name that is used on help messages
 * @property description the description of the command
 * @property usage the full usage of the command
 * @property permission the command permissions
 * @property commandAssociations all alias-command associations of sub-commands
 * @property category the [CommandCategory] of the command
 * @property callback an [Exception] that is supposed to highlight class defention line
 */
abstract class AbstractCommand : CommandRegistry<AbstractSubCommand> {
    open val callback: Exception = Exception()

    override val commandAssociations: MutableMap<String, AbstractSubCommand> = mutableMapOf()

    abstract val aliases: List<String>
    val name: String
        get() = aliases.first()
    abstract val displayName: String
    abstract val description: String
    abstract val usage: String
    abstract val permission: Permission
    abstract val category: CommandCategory

    /**
     * Invokes the command.
     * @param context the [Context] in which the command is invoked
     */
    abstract fun execute(context: Context)

}
