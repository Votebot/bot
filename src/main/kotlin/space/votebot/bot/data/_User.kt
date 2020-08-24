package space.votebot.bot.data

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import space.votebot.bot.data.VoteBotUsers.id
import space.votebot.bot.data.VoteBotUsers.locale
import space.votebot.bot.util.SnowflakeUtil

/**
 * Representation of a User in VoteBot database.
 * @property id the discord id of the user
 * @property locale the selected locale of the user
 */
object VoteBotUsers : IdTable<Long>("users") {

    override val id = long("id").clientDefault { SnowflakeUtil.newId() }.entityId()
    val userId = long("user_id")
    val locale = text("locale").default("en")

    override val primaryKey = PrimaryKey(id)
}

/**
 * Representation of a User in VoteBot database.
 * @property userId the discord id of the user
 * @property locale the selected locale of the user
 */
class VoteBotUser(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<VoteBotUser>(VoteBotUsers) {
        /**
         * Finds a [VoteBotUser] for [id] or creates a new one if necessary.
         */
        fun findByUserIdOrNew(id: Long): VoteBotUser = findById(id) ?: new {
            userId = id
        }
    }

    var userId by VoteBotUsers.userId
    var locale by VoteBotUsers.locale
}
