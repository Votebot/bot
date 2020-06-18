package space.votebot.bot.command.permission

import net.dv8tion.jda.api.entities.ISnowflake
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import space.votebot.bot.command.impl.PermissionChecker
import space.votebot.bot.command.permission.PermissionNodes.entityID
import space.votebot.bot.command.permission.PermissionNodes.id
import space.votebot.bot.command.permission.PermissionNodes.negated
import space.votebot.bot.command.permission.PermissionNodes.node
import space.votebot.bot.command.permission.PermissionNodes.type
import xyz.downgoon.snowflake.Snowflake

private val snowflake = Snowflake(5, 6)

/**
 * Permission type.
 *
 * @property humanName name used in messages
 */
enum class PermissionType(val humanName: String) {
    /**
     * User based permission
     * (prioritised in checks)
     */
    MEMBER("Member"),

    /**
     * Role based permissions
     */
    ROLE("Role")
}

/**
 * Representation of permission nodes table in database.
 *
 * @property id id of the node
 * @property type [PermissionType] of the node
 * @property entityID id of the Discord entity the node is for
 * @property node the name of the node
 * @property negated whether this node is negated or not
 */
object PermissionNodes : IdTable<Long>("permission") {
    override val id: Column<EntityID<Long>> = long("id").clientDefault { snowflake.nextId() }.entityId()

    val type: Column<PermissionType> = enumeration("type", PermissionType::class)
    val entityID: Column<Long> = long("entity_id")
    val node: Column<String> = text("node")
    val negated: Column<Boolean> = bool("negated")
}

/**
 * Representation of permission nodes entity in database.
 *
 * @property id id of the node
 * @property type [PermissionType] of the node
 * @property entityID id of the Discord entity the node is for
 * @property node the name of the node
 * @property negated whether this node is negated or not
 */
class PermissionNode(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PermissionNode>(PermissionNodes) {

        /**
         * Finds a permission node for [snowflake] and [node].
         */
        fun findBySnowflake(snowflake: ISnowflake, node: String): PermissionNode? {
            val type = when (snowflake) {
                is Role -> PermissionType.ROLE
                is Member -> PermissionType.MEMBER
                else -> error("Unsupported entity")
            }
            return find { (entityID eq snowflake.idLong) and (PermissionNodes.type eq type) and ((PermissionNodes.node eq node) or (PermissionNodes.node eq "*")) }.firstOrNull()
        }
    }

    var type: PermissionType by PermissionNodes.type
    var entityID: Long by PermissionNodes.entityID
    var node: String by PermissionNodes.node
    var negated: Boolean by PermissionNodes.negated

    internal val result by lazy { if (negated) PermissionChecker.PermissionResult.DENY else PermissionChecker.PermissionResult.ALLOW }

    /**
     * Human-readable name for messages.
     */
    val humanName: String by lazy { "${if (negated) "!" else ""}$node -> $entityID (${type.humanName})" }
}