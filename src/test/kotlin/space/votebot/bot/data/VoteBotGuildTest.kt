package space.votebot.bot.data

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

internal class VoteBotGuildTest {

    @BeforeEach
    fun setUp() {
        mockDatabase()
    }

    @Test
    fun `test findByGuildIdOrNew`() {
        transaction {
            SchemaUtils.create(VoteBotGuilds)
            val guildId = VoteBotGuilds.insert {
                it[guildId] = 1337
                it[prefix] = "votes!"
                it[disableDefaultPrefix] = true
            } get VoteBotGuilds.id

            val guild = VoteBotGuild.findByGuildIdOrNew(guildId.value)
            assertNotNull(guild, "guild must not be null")
        }
    }

    private fun mockDatabase() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
    }
}