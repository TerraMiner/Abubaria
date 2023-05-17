package d2t.terra.abubaria.io

import jdk.nashorn.internal.AssertsEnabled
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.forEach
import kotlin.time.measureTime

class LagDebugger(private val enabled: Boolean = true) {
    private var startTime = System.nanoTime()
    private val list = mutableMapOf<Int, Long>()

    fun check(id: Int) {
        if (!enabled) return
        list[id] = System.nanoTime() - startTime
        startTime = System.nanoTime()
    }

    fun debug(string: String) {
        if (!enabled) return
        println("===========================")
        list.entries.sortedBy { it.value / 1000000.0 }.reversed().forEach {
            println("$string: ${it.key} - ${it.value / 1000000.0}")
        }
    }
}