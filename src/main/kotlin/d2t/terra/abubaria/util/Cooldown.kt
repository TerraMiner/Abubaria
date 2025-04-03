package d2t.terra.abubaria.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.StringJoiner
import java.util.TimeZone
import kotlin.time.Duration.Companion.milliseconds

class Cooldown(
    private var delay: Long
) : Cloneable {

    var start: Long = 0L
    constructor() : this(0L)

    fun isEnded() = if (isPaused()) false else start + delay < System.currentTimeMillis()

    fun remain() = if (isPaused()) delay else start + delay - System.currentTimeMillis()

    fun isPaused() = start == 0L

    fun endTime() = start + delay

    fun elapsed() = if (isPaused()) 0L else System.currentTimeMillis() - start

    fun start() {
        start = System.currentTimeMillis()
    }

    fun stop() {
        start = 0L
    }

    fun addDelay(value: Long) {
        if (isEnded()) {
            start()
            delay = value
        } else delay += value
    }

    fun setDelay(value: Long) {
        delay = value
    }

    fun getDelay() = delay

    fun pause() {
        setDelay(remain())
        stop()
    }

    fun format() = format(remain().coerceAtLeast(0))

    fun formatEndTime(): String = formatDate(endTime(), "dd.MM.yyyy HH:mm")

    public override fun clone() = Cooldown(delay).also { it.start = start }

    companion object {
        fun format(time: Long, trim: Boolean = true) = StringJoiner(":").apply {
            val totalSeconds = time.milliseconds.inWholeMilliseconds / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            if (!trim || hours > 0) add(hours.twoSym)
            add(minutes.twoSym)
            add(seconds.twoSym)
        }.toString()

        fun formatDate(time: Long, format: String = "dd.MM.yyyy HH:mm"): String = SimpleDateFormat(format).apply {
            timeZone = TimeZone.getTimeZone("Europe/Moscow")
        }.format(Date(time))

        private val Long.twoSym get() = if (this < 10) "0$this" else "$this"
    }

}