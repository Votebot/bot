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

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

class VoteBotGuild(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<VoteBotGuild>(VoteBotGuilds)
    var guildId by VoteBotGuilds.guildId
    var prefix by VoteBotGuilds.prefix
}