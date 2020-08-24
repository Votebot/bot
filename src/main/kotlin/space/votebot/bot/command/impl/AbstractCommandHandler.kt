package space.votebot.bot.command.impl

import com.i18next.java.Operation
import space.votebot.bot.util.Embeds
import space.votebot.bot.event.EventSubscriber
import space.votebot.bot.events.CommandNoPermissionEvent

/**
 * Base for all command handlers.
 */
abstract class AbstractCommandHandler {
    /**
     * Default handler of permission events.
     */
    @EventSubscriber
    fun noPermission(event: CommandNoPermissionEvent) {
        val context = event.context
        val translations = context.translations
        val command = context.command

        context.respond(Embeds.error(
                translations.t("embeds.no_permission.title"),
                translations.t("embeds.no_permission.description", Operation.Interpolation(
                        mapOf("node" to command.permissionNode, "permission" to command.permission.name)
                ))
        )).queue()
    }
}