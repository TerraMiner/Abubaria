package d2t.terra.abubaria.io

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.forEach
import kotlin.time.measureTime

class LagDebugger(private val enabled: Boolean = true, private val minMsDebug: Int = 1) {
    private var startTime = System.nanoTime()
    val list = mutableMapOf<Int, Long>()

    fun check(id: Int) {
        if (!enabled) return
        list[id] = System.nanoTime() - startTime
        startTime = System.nanoTime()
    }

    fun debug(string: String) {
        if (!enabled) return
        val applied = list.entries.map { it.key to it.value / 1000000.0 }
        if (applied.none { it.second >= minMsDebug }) return
        println("===========================")
        applied.sortedBy { it.second }.reversed().forEach {
            println("$string: ${it.first} - ${it.second}")
        }
        list.clear()
    }
}