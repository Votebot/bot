package space.votebot.bot.event

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import mu.KotlinLogging
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.IEventManager
import net.dv8tion.jda.api.hooks.SubscribeEvent
import space.votebot.bot.util.DefaultThreadFactory
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
import kotlin.reflect.full.*

/**
 * @see SubscribeEvent
 */
typealias EventSubscriber = SubscribeEvent

/**
 * Tells the [AnnotatedEventManager] more information about an event.
 * @param callParents whether the event classes' parents should be called or not
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class EventDescriber(val callParents: Boolean)

/**
 * Enhanced Kotlin reimplementation of [net.dv8tion.jda.api.hooks.AnnotatedEventManager] adding ability to use [EventSubscriber].
 */
class AnnotatedEventManager(
        coroutineContext: CoroutineContext = DefaultThreadFactory.newSingleThreadExecutor(
                "EventExecutor"
        ).asCoroutineDispatcher()
) : IEventManager {

    private val logger = KotlinLogging.logger { }
    private val listeners = mutableListOf<Any>()
    private val functions = mutableMapOf<KType, MutableSet<InstanceFunction>>()
    private val coroutine = coroutineContext + CoroutineExceptionHandler { _, throwable ->
        logger.error(throwable) { "Exception caught in Event Listener" }
    }

    /**
     * @see IEventManager.getRegisteredListeners
     */
    override fun getRegisteredListeners(): List<Any> = listeners.toList()

    /**
     * @see IEventManager.register
     */
    override fun register(listener: Any): Unit = if (this.listeners.add(listeners)) {
        addMethods(listener)
    } else Unit

    /**
     * @see IEventManager.unregister
     */
    override fun unregister(listener: Any): Unit = if (listeners.remove(listener)) {
        removeMethods(listener)
    } else Unit

    private fun addMethods(listener: Any) {
        val functions = findSubscriberFunctions(listener::class)
        functions.forEach { function ->
            val eventType = determineEventType(function)
            registerSubscriberMethod(eventType, listener, function)
        }
    }

    private fun removeMethods(listener: Any) {
        val functions = findSubscriberFunctions(listener::class)
        functions.forEach { function ->
            val eventType = determineEventType(function)
            removeSubscriberMethod(eventType, listener)
        }
    }

    private fun findSubscriberFunctions(clazz: KClass<*>) =
            clazz.declaredFunctions.filterNot { it.findAnnotation<EventSubscriber>() == null }

    private fun determineEventType(function: KFunction<*>): KType {
        val parameters = function.valueParameters
        require(parameters.size == 1) {
            "EventSubscriber functions must have exactly one parameter (the event)"
        }
        val eventType = parameters.first().type
        require(eventType.isSubtypeOf(GenericEvent::class.starProjectedType)) { "EventSubscriber parameter must be a subclass of Event" }
        return eventType
    }

    private fun findSubscriberList(eventType: KType) = functions[eventType]

    private fun registerSubscriberMethod(eventType: KType, listener: Any, function: KFunction<*>) {
        val instanceFunction = InstanceFunction(listener, function)
        val functions = functions[eventType]
        if (functions == null) {
            this.functions[eventType] = mutableSetOf(instanceFunction)
        } else {
            functions += instanceFunction
        }
    }

    private fun removeSubscriberMethod(eventType: KType, listener: Any) {
        val list = findSubscriberList(eventType)
                ?: error("There are no subscribers for this event type")
        list.removeIf { it.instance == listener }
    }

    /**
     * @see IEventManager.handle
     */
    override fun handle(event: GenericEvent) {
        val eventType = event::class

        tailrec fun callEvent(eventClass: KClass<*>) {
            val callParents =
                    eventType.findAnnotation<EventDescriber>()?.callParents ?: true
            val functions = this.functions[eventClass.starProjectedType] ?: return
            functions.forEach {
                GlobalScope.launch(coroutine) {
                    it.call(event)
                }
            }
            if (eventType == GenericEvent::class || !callParents) {
                callEvent(eventType.superclasses.first())
            }
        }

        callEvent(eventType)
    }

    private data class InstanceFunction(val instance: Any, private val function: KFunction<*>) {
        init {
            require(function.visibility == KVisibility.PUBLIC) { "Listener function cannot be private" }
        }

        fun call(vararg parameters: Any?) = function.call(instance, *parameters)
    }
}