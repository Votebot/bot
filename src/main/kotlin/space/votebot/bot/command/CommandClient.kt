package space.votebot.bot.command

import kotlin.coroutines.CoroutineContext

/**
 * Parser and manager for [AbstractCommand](commands).
 */
interface CommandClient : CommandRegistry<AbstractCommand> {

    /**
     * The [CoroutineContext] used to execute commands.
     */
    val executor: CoroutineContext

    /**
     * The [PermissionHandler] used for handling command permissions.
     * @see PermissionHandler
     */
    val permissionHandler: PermissionHandler

}
