import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.IEventManager
import net.dv8tion.jda.api.requests.RestAction
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import space.votebot.bot.command.AbstractCommand
import space.votebot.bot.command.AbstractSubCommand
import space.votebot.bot.command.CommandCategory
import space.votebot.bot.command.context.Context
import space.votebot.bot.command.impl.CommandClientImpl
import space.votebot.bot.command.permission.Permission
import space.votebot.bot.constants.Constants
import space.votebot.bot.core.VoteBot
import space.votebot.bot.database.VoteBotGuilds
import space.votebot.bot.event.AnnotatedEventManager
import space.votebot.bot.event.EventSubscriber
import space.votebot.bot.events.CommandErrorEvent
import space.votebot.bot.util.NopInfluxDBConnection
import space.votebot.bot.util.asMention
import java.util.concurrent.CompletableFuture
import java.util.function.BooleanSupplier
import java.util.function.Consumer

class CommandTest {

    @Test
    fun `check prefixed normal command`() {
        val message = mockMessage {
            on { contentRaw }.thenReturn("v!test ${arguments.joinToString(" ")}".mock())
        }

        val command = mockCommand {
            on { aliases }.thenReturn(listOf("test"))
        }

        ensureCommandCall(message, command, arguments)
    }

    @Test
    fun `check mentioned normal command`() {
        val mention = selfMember.asMention()
        val message = mockMessage {
            on { contentRaw }.thenReturn("$mention test ${arguments.joinToString(" ")}".mock())
        }

        val command = mockCommand {
            on { aliases }.thenReturn(listOf("test"))
        }

        ensureCommandCall(message, command, arguments)
    }

    @Test
    fun `check prefixed sub command`() {
        val message = mockMessage {
            on { contentRaw }.thenReturn("v!test ${arguments.joinToString(" ")}".mock())
        }

        val subCommand = mock<AbstractSubCommand> {
            on { permission }.thenReturn(Permission.ANY)
        }

        val command = mockCommand {
            on { aliases }.thenReturn(listOf("test"))
            on { commandAssociations }.thenReturn(mutableMapOf("sub" to subCommand))
        }

        ensureCommandCall(message, command, arguments.subList(1, arguments.size), subCommand)
    }

    @Test
    fun `check mentioned sub command`() {
        val mention = selfMember.asMention()
        val message = mockMessage {
            on { contentRaw }.thenReturn("$mention test ${arguments.joinToString(" ")}".mock())
        }

        val subCommand = mock<AbstractSubCommand> {
            on { permission }.thenReturn(Permission.ANY)
        }

        val command = mockCommand {
            on { aliases }.thenReturn(listOf("test"))
            on { commandAssociations }.thenReturn(mutableMapOf("sub" to subCommand))
        }

        ensureCommandCall(message, command, arguments.subList(1, arguments.size), subCommand)
    }

    @Test
    fun `check command error handling`() {
        val error = RuntimeException("Test error")
        val command = object : AbstractCommand() {
            override val aliases: List<String> = listOf("test")
            override val displayName: String
                get() = TODO("Not yet implemented")
            override val description: String
                get() = TODO("Not yet implemented")
            override val usage: String
                get() = TODO("Not yet implemented")
            override val permission: Permission = Permission.ANY
            override val category: CommandCategory
                get() = TODO("Not yet implemented")

            override suspend fun execute(context: Context) = throw error

        }

        val listener: Validator = mock()
        eventManager.register(listener)
        val message = mockMessage {
            on { contentRaw }.thenReturn("v!test ${arguments.joinToString(" ")}".mock())
        }
        val event = GuildMessageReceivedEvent(jda, 200, message)

        client.registerCommands(command)
        client.onMessage(event)

        verify(listener).onError(argThat {
            this.error === error
        })
    }

    private interface Validator {
        @EventSubscriber
        fun onError(event: CommandErrorEvent)
    }

    private fun ensureCommandCall(
            message: Message,
            command: AbstractCommand,
            arguments: List<String>,
            subCommand: AbstractSubCommand? = null
    ) {
        val event = GuildMessageReceivedEvent(jda, 200, message)
        client.registerCommands(command)
        client.onMessage(event)
        val actualCommand = subCommand ?: command
        runBlocking {
            verify(actualCommand).execute(argThat {
                arguments == args.toList() &&
                        client === this.commandClient &&
                        message === this.message &&
                        bot === this.bot &&
                        author === this.author
            })
        }
    }

    private fun mockMessage(
            stubbing: KStubbing<Message>.() -> Unit
    ) =
            mock<Message> {
                on { this.author }.thenReturn(author)
                on { textChannel }.thenReturn(channel)
                on { contentRaw }.thenReturn("!test ${arguments.joinToString(" ")}")
                on { isWebhookMessage }.thenReturn(false)
                on { member }.thenReturn(selfMember)
                on { this.guild }.thenReturn(guild)
                on { jda }.thenReturn(jda)
                stubbing(this)
            }

    private fun mockCommand(
            stubbing: KStubbing<AbstractCommand>.() -> Unit
    ) = mock<AbstractCommand> {
        on { permission }.thenReturn(Permission.ANY)
        stubbing(this)
    }

    companion object {
        private lateinit var bot: VoteBot
        private val arguments = listOf("sub", "2", "3")
        private val influx = NopInfluxDBConnection()
        private lateinit var jda: JDA
        private lateinit var channel: TextChannel
        private lateinit var selfMember: Member
        private lateinit var guild: Guild
        private lateinit var client: CommandClientImpl
        private lateinit var author: User
        private val eventManager: IEventManager = AnnotatedEventManager(Dispatchers.Unconfined)

        @BeforeAll
        @JvmStatic
        @Suppress("unused")
        fun `setup database`() {
            Database.connect("jdbc:h2:~/tmp/test.db", driver = "org.h2.Driver")
            transaction {
                SchemaUtils.create(VoteBotGuilds)
            }
        }

        @BeforeAll
        @JvmStatic
        @Suppress("unused")
        fun `setup mock objects`() {
            bot = mock {
                on { this.influx }.thenReturn(influx)
                on { eventManager }.thenReturn(eventManager)
            }
            client = CommandClientImpl(bot, Constants.prefix, Dispatchers.Unconfined)
            jda = mock()

            selfMember = mock {
                on { id }.thenReturn("123456789")
                on { hasPermission(any<GuildChannel>(), eq(net.dv8tion.jda.api.Permission.MESSAGE_WRITE)) }.thenReturn(true)
                on { hasPermission(any<GuildChannel>(), eq(net.dv8tion.jda.api.Permission.MESSAGE_EMBED_LINKS)) }.thenReturn(true)
            }
            guild = mock {
                on { this.selfMember }.thenReturn(selfMember)
            }
            channel = mock {
                on { sendTyping() }.thenReturn(EmptyRestAction<Void>())
                on { guild }.thenReturn(guild)
            }
            author = mock {
                on { isBot }.thenReturn(false)
                on { isFake }.thenReturn(false)
            }
        }
    }
}

fun String.mock() = mapIndexed { index, it -> if (index % 2 == 0) it.toUpperCase() else it.toLowerCase() }.joinToString("")

private class EmptyRestAction<T> : RestAction<T> {
    override fun submit(shouldQueue: Boolean): CompletableFuture<T> = TODO("not implemented")

    override fun complete(shouldQueue: Boolean): T = TODO("not implemented")

    override fun getJDA(): JDA = TODO("not implemented")

    override fun queue(success: Consumer<in T?>?, failure: Consumer<in Throwable>?) = success?.accept(null)
            ?: Unit

    override fun setCheck(checks: BooleanSupplier?): RestAction<T> = this

}
