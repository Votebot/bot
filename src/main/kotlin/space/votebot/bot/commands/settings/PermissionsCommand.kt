package space.votebot.bot.commands.settings

import com.i18next.java.Operation
import net.dv8tion.jda.api.entities.Role
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import space.votebot.bot.command.AbstractCommand
import space.votebot.bot.command.CommandCategory
import space.votebot.bot.command.context.Context
import space.votebot.bot.command.permission.Permission
import space.votebot.bot.command.permission.PermissionNode
import space.votebot.bot.command.permission.PermissionNodes
import space.votebot.bot.command.permission.PermissionType
import space.votebot.bot.constants.Embeds

/**
 * Permissions command (better than iPad).
 */
class PermissionsCommand : AbstractCommand() {
    override val aliases: List<String> = listOf("permissions", "permission", "perms", "perm")
    override val displayName: String = "Permissions"
    override val usage: String = "<allow/deny/neutral> <@Role/@User> <node>"
    override val permission: Permission = Permission.ADMIN
    override val category: CommandCategory = CommandCategory.SETTINGS

    override suspend fun execute(context: Context) {
        val args = context.args
        val modeRaw = args.requiredArgument(0, context) ?: return
        val mode = try {
            Mode.valueOf(modeRaw.toUpperCase())
        } catch (e: IllegalArgumentException) {
            return context.respond(Embeds.error(
                    context.translations.t("commands.permissions.invalid_mode.title"),
                    context.translations.t("commands.permissions.invalid_mode.description"))
            ).queue()
        }

        val target = args.optionalRole(1, true, context.guild)
                ?: args.optionalUser(1, true, context.jda)
                ?: return context.respond(
                        Embeds.error(
                                context.translations.t("commands.permissions.invalid_target.title"),
                                context.translations.t("commands.permissions.invalid_target.description"))
                ).queue()

        val node = args.requiredArgument(2, context) ?: return

        val type = if (target is Role) PermissionType.ROLE else PermissionType.MEMBER
        val permissionNode = transaction {
            PermissionNodes.deleteWhere { (PermissionNodes.entityID eq target.idLong) and (PermissionNodes.type eq type) and (PermissionNodes.node eq node) }
            if (mode == Mode.NEUTRAL) return@transaction null
            PermissionNode.new {
                entityID = target.idLong
                this.node = node
                this.type = type
                this.negated = mode == Mode.DENY
            }
        }

        context.respond(Embeds.success(
                context.translations.t("commands.permissions.success.title"),
                context.translations.t("commands.permissions.success.description", Operation.Interpolation(
                        "node",
                        permissionNode?.humanName
                                ?: "$node? -> ${target.idLong} (${type.humanName})"
                ))
        )).queue()
    }

    private enum class Mode {
        ALLOW,
        DENY,
        NEUTRAL
    }
}
