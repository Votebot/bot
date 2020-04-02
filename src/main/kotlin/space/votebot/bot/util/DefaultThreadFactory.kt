package space.votebot.bot.util

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * A modified version of [java.util.concurrent.Executors] default thread factory with custom name support.
 * Format `name-pool-poolNumber-thread-threadNumber`
 *
 * @see java.util.concurrent.Executors
 */
class DefaultThreadFactory(poolName: String?) : ThreadFactory {
    private val group: ThreadGroup
    private val threadNumber = AtomicInteger(1)
    private val namePrefix: String

    init {
        val s = System.getSecurityManager()
        group = if (s != null) s.threadGroup else Thread.currentThread().threadGroup
        namePrefix = (if (poolName != null) "$poolName-" else "") + "pool-" +
                poolNumber.getAndIncrement() +
                "-thread-"
    }

    /**
     * Creates a new [Thread] and returns it
     * @return The newly created [Thread]
     * @see Thread
     */
    override fun newThread(runnable: Runnable): Thread {
        val t = Thread(
                group, runnable,
                namePrefix + threadNumber.getAndIncrement(),
                0
        )
        if (t.isDaemon) t.isDaemon = false
        if (t.priority != Thread.NORM_PRIORITY) t.priority = Thread.NORM_PRIORITY
        return t
    }

    companion object {
        private val poolNumber = AtomicInteger(1)

        /**
         * Creates a new [Executors.newSingleThreadExecutor] using [poolName].
         */
        fun newSingleThreadExecutor(poolName: String?): ExecutorService =
                Executors.newSingleThreadExecutor(DefaultThreadFactory(poolName))

        /**
         * Creates a new [Executors.newSingleThreadExecutor] using [poolName] with [threads] fixed threads.
         */
        fun newFixedThreadExecutor(poolName: String?, threads: Int): ExecutorService =
                Executors.newFixedThreadPool(threads, DefaultThreadFactory(poolName))
    }
}
