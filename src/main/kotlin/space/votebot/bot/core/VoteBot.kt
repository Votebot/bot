package space.votebot.bot.core

import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import space.votebot.bot.config.Config

class VoteBot(private val config: Config) {

    private lateinit var shardManager: ShardManager

    fun start() {
        val shardManagerBuilder = DefaultShardManagerBuilder.createDefault(config.discordToken)
        shardManagerBuilder.addEventListeners()
        shardManager = shardManagerBuilder.build()
    }
}