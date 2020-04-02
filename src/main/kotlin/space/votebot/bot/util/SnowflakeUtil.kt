package space.votebot.bot.util

import xyz.downgoon.snowflake.Snowflake

class SnowflakeUtil {

    companion object {
        private val snowflake = Snowflake(1, 1)

        fun newId() = snowflake.nextId()
    }
}