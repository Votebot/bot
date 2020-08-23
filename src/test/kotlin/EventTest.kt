import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.Dispatchers
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.Event
import net.dv8tion.jda.api.events.ExceptionEvent
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.IEventManager
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import space.votebot.bot.event.*

class EventTest {

    @Test
    fun `call normal event`() {
        val validator = mock<Validator>()
        eventManager.register(Listener(validator))

        ensureEventCall(event, validator)
    }

    @Test
    fun `call normal event with extended listener`() {
        val validator = mock<Validator>()
        val listener = eventManager.hear<ExceptionEvent> { validator.onEvent(it) }
        eventManager.register(listener)

        ensureEventCall(event, validator)
    }

    // Kotlin compiler errors here DK WHY
//    @Test
//    fun `expect event`() {
//        val validator = mock<Validator>()
//        eventManager.expect<ExceptionEvent>(timeout = 10) {
//            validator.onEvent(it)
//        }
//        ensureEventCall(event, validator)
//    }

    private fun ensureEventCall(event: Event, validator: Validator) {
        eventManager.handle(event)
        verify(validator).onEvent(event)
    }

    companion object {
        private lateinit var eventManager: IEventManager
        private lateinit var jda: JDA
        private lateinit var event: Event

        @BeforeAll
        @JvmStatic
        @Suppress("unused")
        fun `setup objects `() {
            eventManager = AnnotatedEventManager(Dispatchers.Unconfined)
            jda = mock()
            event = ExceptionEvent(jda, RuntimeException("ERROR"), true)
        }
    }

}

interface Validator {
    fun onEvent(event: GenericEvent)
}

class Listener(private val validator: Validator) {

    @EventSubscriber
    fun onEvent(event: ExceptionEvent) {
        validator.onEvent(event)
    }
}