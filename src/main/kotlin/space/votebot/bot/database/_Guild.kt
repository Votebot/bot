package space.votebot.bot.database

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import space.votebot.bot.util.SnowflakeUtil

object VoteBotGuilds : IdTable<Long>("guilds") {
    override val id = long("id").clientDefault { SnowflakeUtil.newId() }.entityId()
    var guildId = long("guild_id")
    val prefix = varchar("prefix", 5).default("v!")
    val disableDefaultPrefix = bool("disable_default_prefix").default(false)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

class VoteBotGuild(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<VoteBotGuild>(VoteBotGuilds) {
        fun findByGuildIdOrNew(id: Long): VoteBotGuild = find { VoteBotGuilds.guildId eq id }.firstOrNull() ?: new {
            guildId = id
        }
    }

    var guildId by VoteBotGuilds.guildId
    var prefix by VoteBotGuilds.prefix
    var disableDefaultPrefix by VoteBotGuilds.disableDefaultPrefix
}