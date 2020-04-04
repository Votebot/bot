package space.votebot.bot.core

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.sharding.ShardManager
import space.votebot.bot.config.Config
import space.votebot.bot.util.DefaultThreadFactory
import java.time.Duration
import java.util.*

/**
 * Game animator.
 */
class GameAnimator(private val discord: Discord, config: Config) {

    private val context = DefaultThreadFactory.newSingleThreadExecutor("GameAnimator").asCoroutineDispatcher()
    private val games: List<AnimatedGame> = config.rawGameAnimatorGames.map {
        val split = it.split(':')
        val (status, type, text) = split
        AnimatedGame(OnlineStatus.valueOf(status.toUpperCase()), Activity.ActivityType.valueOf(type.toUpperCase()), text)
    }

    private val queue: Queue<AnimatedGame> = LinkedList()
    private lateinit var job: Job

    fun start() {
        job = GlobalScope.launch(context) {
            while (true) {
                queue.add(games.random())
                queue.poll().apply {
                    apply(discord.shardManager)
                    delay(duration)
                }
            }
        }
    }

    /**
     * Pushes a [game] at the queue.
     */
    fun push(game: AnimatedGame) = queue.add(game)

    /**
     * Stops the animation and closes all needed resources.
     */
    fun stop() {
        job.cancel()
        context.close()
    }
}

data class AnimatedGame(val status: OnlineStatus = OnlineStatus.ONLINE, val type: Activity.ActivityType = Activity.ActivityType.DEFAULT, val text: String, val duration: Duration = Duration.ofSeconds(30)) {
    fun apply(shardManager: ShardManager) {
        val text = this.text
                .replace("%users%", shardManager.users.size.toString())
                .replace("%guilds%", shardManager.guilds.size.toString())
        shardManager.setPresence(status, Activity.of(type, text))
    }
}
