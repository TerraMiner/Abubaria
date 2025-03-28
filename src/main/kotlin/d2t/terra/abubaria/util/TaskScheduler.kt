package d2t.terra.abubaria.util

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.atomic.AtomicBoolean

object TaskScheduler {
    private val mainThreadTasks = ConcurrentLinkedQueue<() -> Unit>()

    private val initialized = AtomicBoolean(false)

    private var mainThreadId: Long = -1

    private val asyncExecutor = Executors.newScheduledThreadPool(
        Runtime.getRuntime().availableProcessors(),
        { r ->
            val thread = Thread(r, "Async-Scheduler-Thread")
            thread.isDaemon = true
            thread
        }
    )

    fun initialize() {
        if (initialized.compareAndSet(false, true)) {
            mainThreadId = Thread.currentThread().id
            println("TaskScheduler инициализирован в потоке ${Thread.currentThread().name} (ID: $mainThreadId)")
        } else {
            println("TaskScheduler уже инициализирован")
        }
    }

    fun tick() {
        if (!initialized.get()) {
            throw IllegalStateException("TaskScheduler не инициализирован. Вызовите initialize() из главного потока LWJGL.")
        }

        if (Thread.currentThread().id != mainThreadId) {
            throw IllegalStateException("update() должен вызываться только из главного потока LWJGL")
        }

        var task = mainThreadTasks.poll()
        while (task != null) {
            try {
                task()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            task = mainThreadTasks.poll()
        }
    }

    fun every(delay: Long, period: Long, timeUnit: TimeUnit = TimeUnit.MILLISECONDS, action: () -> Unit): ScheduledFuture<*> {
        if (!initialized.get()) {
            throw IllegalStateException("TaskScheduler не инициализирован. Вызовите initialize() из главного потока LWJGL.")
        }

        return asyncExecutor.scheduleAtFixedRate({
            mainThreadTasks.add {
                try {
                    action()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, delay, period, timeUnit)
    }

    fun everyAsync(delay: Long, period: Long, timeUnit: TimeUnit = TimeUnit.MILLISECONDS, action: () -> Unit): ScheduledFuture<*> {
        return asyncExecutor.scheduleAtFixedRate({
            try {
                action()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, delay, period, timeUnit)
    }

    fun after(delay: Long = 0, timeUnit: TimeUnit = TimeUnit.MILLISECONDS, action: () -> Unit): ScheduledFuture<*> {
        if (!initialized.get()) {
            throw IllegalStateException("TaskScheduler не инициализирован. Вызовите initialize() из главного потока LWJGL.")
        }

        return asyncExecutor.schedule({
            mainThreadTasks.add {
                try {
                    action()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, delay, timeUnit)
    }

    fun afterAsync(delay: Long = 0, timeUnit: TimeUnit = TimeUnit.MILLISECONDS, action: () -> Unit): ScheduledFuture<*> {
        return asyncExecutor.schedule({
            try {
                action()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, delay, timeUnit)
    }

    fun isMainThread(): Boolean {
        return Thread.currentThread().id == mainThreadId
    }

    fun shutdown() {
        asyncExecutor.shutdown()

        try {
            if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                asyncExecutor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            asyncExecutor.shutdownNow()
            Thread.currentThread().interrupt()
        }

        mainThreadTasks.clear()
        initialized.set(false)
    }
}

