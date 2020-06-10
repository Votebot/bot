package space.votebot.bot.command.impl

import com.influxdb.client.write.Point
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent
import org.jetbrains.exposed.sql.transactions.transaction
import space.votebot.bot.command.AbstractCommand
import space.votebot.bot.command.CommandClient
import space.votebot.bot.command.PermissionHandler
import space.votebot.bot.command.context.Arguments
import space.votebot.bot.command.context.Context
import space.votebot.bot.core.VoteBot
import space.votebot.bot.database.VoteBotGuild
import space.votebot.bot.event.EventSubscriber
import space.votebot.bot.events.CommandErrorEvent
import space.votebot.bot.events.CommandExecutedEvent
import space.votebot.bot.events.CommandNoPermissionEvent
import space.votebot.bot.util.DefaultThreadFactory
import space.votebot.bot.util.asMention
import space.votebot.bot.util.hasSubCommands
import space.votebot.bot.util.iHavePermission
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

/**
 * Default implementation of [CommandClient].
 * @param bot the current bot instance
 * @param prefix the prefix used for commands
 */
@Suppress("unused")
class CommandClientImpl(
        private val bot: VoteBot, private val prefix: String, override val executor: CoroutineContext =
                Executors.newFixedThreadPool(
                        5,
                        DefaultThreadFactory("CommandExecution")
                ).asCoroutineDispatcher()
) : CommandClient {

    private val delimiter = "\\s+".toRegex()
    private val commandCounter = AtomicInteger()

    override val permissionHandler: PermissionHandler = PermissionChecker()
    override val commandAssociations: MutableMap<String, AbstractCommand> = mutableMapOf()

    /**
     * Listens for message updates.
     */
    @EventSubscriber
    fun onMessageEdit(event: GuildMessageUpdateEvent) {
        if (Duration.between(event.message.timeCreated, OffsetDateTime.now()) > Duration.of(
                        30,
                        ChronoUnit.SECONDS
                )
        ) return
        dispatchCommand(event.message, event.responseNumber)
    }

    /**
     * Listens for new messages.
     */
    @EventSubscriber
    fun onMessage(event: GuildMessageReceivedEvent) = dispatchCommand(event.message, event.responseNumber)

    private fun dispatchCommand(message: Message, responseNumber: Long) {
        if (!message.textChannel.iHavePermission(Permission.MESSAGE_WRITE)) {
            return message.author.openPrivateChannel().map {
                it.sendMessage("Hey! It seems like I don't have the `MESSAGE_WRITE` permission in ${message.textChannel.asMention}. Please make sure that I have those permissions or try in another channel.")
            }.queue()
        }

        if (!message.textChannel.iHavePermission(Permission.MESSAGE_EMBED_LINKS)) {
            return message.channel.sendMessage("Hey it looks like I don't have the `MESSAGE_EMBED_LINKS` permission in ${message.textChannel.asMention}. Please make sure that I have those permissions or try in another channel.").queue()
        }

        val author = message.author

        if (message.isWebhookMessage or author.isBot or author.isFake) return

        return parseCommand(message, responseNumber)
    }

    private fun parseCommand(message: Message, responseNumber: Long) {
        val rawInput = message.contentRaw.toLowerCase()
        val prefix = resolvePrefix(message.guild, rawInput) ?: return

        val nonPrefixedInput = rawInput.substring(prefix).trim()

        val (command, arguments, parent) = resolveCommand(nonPrefixedInput)
                ?: return // No command found

        message.textChannel.sendTyping() // since rest actions are async, we need to wait for send typing to succeed
                .queue(fun(_: Void?) { // Since Void has a private constructor JDA passes in null, so it has to be nullable even if it is not used
                    val context = Context(bot, command, arguments, message, this, responseNumber)
                    @Suppress("ReplaceNotNullAssertionWithElvisReturn") // Cannot be null in this case since it is send from a TextChannel
                    if (!permissionHandler.isCovered(
                                    command.permission,
                                    message.member!!
                            )
                    ) return bot.eventManager.handle(CommandNoPermissionEvent(context.jda, context.responseNumber, context.message, context))
                    val t1 = Instant.now()
                    processCommand(command, context)
                    val t2 = Instant.now()
                    GlobalScope.launch {
                        bot.influx.writePoint(Point.measurement("commands_executed")
                                .addTag("command", parent?.name ?: command.name)
                                .addField("duration", t2.minusMillis(t1.toEpochMilli()).toEpochMilli())
                                .addField("count", commandCounter.incrementAndGet())
                        )
                    }
                })
    }

    private fun processCommand(command: AbstractCommand, context: Context) {
        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            bot.eventManager.handle(CommandErrorEvent(throwable, coroutineContext, Thread.currentThread(), context.jda, context.responseNumber, context.message, context))
        }

        GlobalScope.launch(executor + exceptionHandler) {
            command.execute(context)
            bot.eventManager.handle(CommandExecutedEvent(context.jda, context.responseNumber, context.message, context))
        }
    }

    private fun resolveCommand(input: String): CommandContainer? {
        tailrec fun findCommand(
                arguments: Arguments,
                associations: Map<String, AbstractCommand>,
                command: AbstractCommand?,
                rootCommand: AbstractCommand?
        ): CommandContainer? {
            // Get invoke
            val invoke = arguments.first()
            // Search command associated with invoke or return previously found command
            val foundCommand = associations[invoke]
                    ?: return command?.let { CommandContainer(it, arguments, rootCommand) }
            // Cut off invoke
            val newArgs = Arguments(arguments.drop(1), raw = arguments.join().substring(invoke.length).trim())
            // Look for sub commands
            if (foundCommand.hasSubCommands() and newArgs.isNotEmpty()) {
                return findCommand(newArgs, foundCommand.commandAssociations, foundCommand, rootCommand
                        ?: foundCommand)
            }
            // Return command if now sub-commands were found
            return CommandContainer(foundCommand, newArgs, rootCommand)
        }

        return findCommand(Arguments(input.trim().split(delimiter), raw = input), commandAssociations, null, null)
    }

    private fun resolvePrefix(guild: Guild, content: String): Int? {
        val voteBotGuild: VoteBotGuild by lazy {
            transaction {
                VoteBotGuild.findByGuildIdOrNew(guild.idLong)
            }
        }
        val mention = guild.selfMember.asMention()
        return when {
            content.startsWith(mention) -> mention.length
            content.startsWith(voteBotGuild.prefix) -> voteBotGuild.prefix.length
            else -> if (!voteBotGuild.disableDefaultPrefix && content.startsWith(prefix)) return prefix.length else null
        }
    }
}

private data class CommandContainer(val command: AbstractCommand, val args: Arguments, val parent: AbstractCommand?)