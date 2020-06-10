package space.votebot.bot.event

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import net.dv8tion.jda.api.events.Event
import net.dv8tion.jda.api.hooks.IEventManager
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

/**
 * [CoroutineContext] used for expecting events.
 */
val expecter: CoroutineContext = Executors.newSingleThreadScheduledExecutor().asCoroutineDispatcher()

/**
 * Interface for a listener for events of type [T].
 */
interface ExtendedListener<in T : Event> {

    fun onEvent(event: T)

    fun unregister(eventManager: IEventManager) = eventManager.unregister(this)
}

/**
 * Creates a listener for event [T].
 * @param action action to run when the event gets fired.
 */
inline fun <reified T : Event> listener(crossinline action: ExtendedListener<T>.(T) -> Unit) = object : ExtendedListener<T> {
    @EventSubscriber
    override fun onEvent(event: T) {
        action(event)
    }

}

/**
 * Registers an event
 * @see listener
 */
inline fun <reified T : Event> IEventManager.hear(crossinline action: ExtendedListener<T>.(T) -> Unit): ExtendedListener<T> =
        listener(action).also(::register)

/**
 * An listener that expects an event.
 */
interface ExpectedListener<T : Event> : ExtendedListener<T> {

    /**
     * Stops waiting for the event.
     */
    fun cancel()

}

/**
 * Expects first event of type [T] the [predicate] matches.
 *
 * @param timeout A timeout after which the expectation get's cancelled
 * @param timeoutUnit The [TemporalUnit] the [timeout] is provided in
 * @param timeoutAction An action that runs if the expectation times out
 * @param action An action that runs for the first event that matches [predicate]
 */
inline fun <reified T : Event> IEventManager.expect(
        crossinline predicate: (T) -> Boolean = { true },
        timeout: Long = 0,
        timeoutUnit: TemporalUnit = ChronoUnit.SECONDS,
        crossinline timeoutAction: () -> Unit = {},
        crossinline action: (T) -> Unit
): ExpectedListener<T> {
    require(timeout >= 0) { "Timeout cannot be negative" }
    var timeoutKeeper: Job? = null
    val listener = hear<T> {
        if (predicate(it)) {
            action(it)
            this.unregister(this@expect)
            timeoutKeeper?.cancel()
        }
    }
    if (timeout > 0) {
        timeoutKeeper = GlobalScope.launch(expecter) {
            delay(Duration.of(timeout, timeoutUnit))
            listener.unregister(this@expect)
            timeoutAction()
        }
    }

    return object : ExpectedListener<T> {
        override fun onEvent(event: T) = listener.onEvent(event)

        override fun cancel() {
            timeoutKeeper?.cancel()
            unregister(this@expect)
        }
    }
}
