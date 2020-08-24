package space.votebot.bot.command.impl

import net.dv8tion.jda.api.entities.ISnowflake
import net.dv8tion.jda.api.entities.Member
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import space.votebot.bot.command.AbstractCommand
import space.votebot.bot.command.PermissionHandler
import space.votebot.bot.command.permission.Permission
import space.votebot.bot.command.permission.PermissionNode
import space.votebot.bot.command.permission.PermissionNodes
import space.votebot.bot.command.permission.PermissionType
import space.votebot.bot.util.Constants

/**
 * Default implementation of [PermissionHandler].
 */
class PermissionChecker : PermissionHandler {
    override fun isCovered(command: AbstractCommand, executor: Member): Boolean {
        if (executor.idLong in Constants.BOT_OWNERS) return true
        val permission = command.permission
        if (executor.hasPermission(net.dv8tion.jda.api.Permission.MANAGE_SERVER)) return permission != Permission.BOT_OWNER
        return when (isPrivileged(command, executor)) {
            PermissionResult.ALLOW -> true
            PermissionResult.DENY -> false
            PermissionResult.NEUTRAL -> permission == Permission.ANY
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun isPrivileged(command: AbstractCommand, executor: Member): PermissionResult {
        val node = transaction { PermissionNode.findBySnowflake(executor, command.permissionNode) }
        if (node != null) return node.result

        val roleIds = executor.roles.map(ISnowflake::getIdLong)
        val roleNode = transaction { PermissionNode.find { (PermissionNodes.type eq PermissionType.ROLE) and (PermissionNodes.entityID inList roleIds) and ((PermissionNodes.node eq command.permissionNode) or (PermissionNodes.node eq "*")) }.limit(1).firstOrNull() }
                ?: return PermissionResult.NEUTRAL

        return roleNode.result
    }

    internal enum class PermissionResult {
        ALLOW,
        DENY,
        NEUTRAL
    }
}