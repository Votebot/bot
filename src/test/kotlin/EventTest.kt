import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.Dispatchers
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.ExceptionEvent
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.IEventManager
import org.junit.jupiter.api.Test
import space.votebot.bot.event.AnnotatedEventManager
import space.votebot.bot.event.EventSubscriber


class EventTest {
    @Test
    fun test() {
        val eventManager: IEventManager = AnnotatedEventManager(Dispatchers.Unconfined)
        val validator = mock<Validator>()
        val jda = mock<JDA>()
        val event = ExceptionEvent(jda, RuntimeException("ERROR"), true)
        eventManager.register(Listener(validator))
        eventManager.handle(event)
        verify(validator).onEvent(event)
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