package space.votebot.bot.data

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import space.votebot.bot.util.SnowflakeUtil

/**
 * Represents the database table for the [VoteBotGuild] model.
 */
object VoteBotGuilds : IdTable<Long>("guilds") {
    override val id = long("id").clientDefault { SnowflakeUtil.newId() }.entityId()
    var guildId = long("guild_id")
    val prefix = varchar("prefix", 10).default("v!")
    val disableDefaultPrefix = bool("disable_default_prefix").default(false)

    override val primaryKey = PrimaryKey(id)
}

/**
 * Represents the database model for guild settings.
 */
class VoteBotGuild(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<VoteBotGuild>(VoteBotGuilds) {
        fun findByGuildIdOrNew(id: Long): VoteBotGuild = find { VoteBotGuilds.guildId eq id }.firstOrNull() ?: new {
            guildId = id
        }
    }

    /**
     * The id of the guild.
     */
    var guildId by VoteBotGuilds.guildId

    /**
     * The prefix of the guild.
     */
    var prefix by VoteBotGuilds.prefix

    /**
     * Whether the bot should react to the default prefix.
     */
    var disableDefaultPrefix by VoteBotGuilds.disableDefaultPrefix
}
