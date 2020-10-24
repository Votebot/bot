package space.votebot.bot.command

/**
 * A registry of [AbstractCommand]s.
 * @property commandAssociations associations between the commands triggers, and the commands
 * @property registeredCommands all registered commands
 * @param T the type of command
 */
interface CommandRegistry<T : AbstractCommand> {
    val commandAssociations: MutableMap<String, T>
    val registeredCommands: List<T>
        get() = commandAssociations.values.distinct()

    private fun registerCommand(command: T) =
            command.aliases.associateWithTo(commandAssociations) { command }

    /**
     * Registers the [commands].
     */
    fun registerCommands(vararg commands: T): Unit = commands.forEach { registerCommand(it) }

    /**
     * Unregisters the [command].
     * @return whether a command got removed or not
     */
    fun unregisterCommand(command: T): Boolean {
        var removed = false
        commandAssociations.forEach { (alias, foundCommand) ->
            run {
                if (foundCommand == command) {
                    commandAssociations.remove(alias)
                    removed = true
                }
            }
        }
        return removed
    }
}
