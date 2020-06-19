package space.votebot.bot.database

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import space.votebot.bot.database.VoteBotUsers.id
import space.votebot.bot.database.VoteBotUsers.locale

/**
 * Representation of a User in VoteBot database.
 * @property id the discord id of the user
 * @property locale the selected locale of the user
 */
object VoteBotUsers : IdTable<Long>("users") {

    override val id: Column<EntityID<Long>> = long("id").entityId()
    val locale: Column<String> = text("locale").default("en")
}

/**
 * Representation of a User in VoteBot database.
 * @property discordId the discord id of the user
 * @property locale the selected locale of the user
 */
class VoteBotUser(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<VoteBotUser>(VoteBotUsers) {
        /**
         * Finds a [VoteBotUser] for [id] or creates a new one if necessary.
         */
        fun findOrCreate(id: Long): VoteBotUser = findById(id) ?: new(id) { }
    }

    val discordId: Long
        get() = id.value
    var locale: String by VoteBotUsers.locale
}
