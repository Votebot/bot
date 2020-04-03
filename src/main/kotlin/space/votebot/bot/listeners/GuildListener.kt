package space.votebot.bot.listeners

import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import space.votebot.bot.database.VoteBotGuild
import space.votebot.bot.database.VoteBotGuilds

class GuildListener {

    @SubscribeEvent
    fun onGuildJoin(event: GuildJoinEvent) {
        transaction {
            if (!VoteBotGuild.find { VoteBotGuilds.guildId eq event.guild.idLong }.any()) {
                VoteBotGuild.new {
                    guildId = event.guild.idLong
                }
            }
        }
    }

    @SubscribeEvent
    fun onGuildLeave(event: GuildLeaveEvent) {
        transaction {
            VoteBotGuilds.deleteWhere { VoteBotGuilds.guildId eq event.guild.idLong }
        }
    }
}