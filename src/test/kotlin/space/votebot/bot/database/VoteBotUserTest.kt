package space.votebot.bot.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

internal class VoteBotUserTest {

    @BeforeEach
    fun setUp() {
        mockDatabase()
    }

    @Test
    fun `test findByUserIdOrNew`() {
        transaction {
            SchemaUtils.create(VoteBotUsers)
            val userId = VoteBotUsers.insert {
                it[userId] = 1337
                it[locale] = "de"
            } get VoteBotUsers.id

            val user = VoteBotUser.findByUserIdOrNew(userId.value)
            assertNotNull(user, "user must not be null")
        }
    }

    private fun mockDatabase() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
    }
}