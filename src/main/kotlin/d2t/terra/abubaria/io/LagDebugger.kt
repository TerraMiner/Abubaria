package d2t.terra.abubaria.io

class LagDebugger {
    private var startTime = System.nanoTime()
    private val list = mutableMapOf<Int, Long>()
    var enabled = true
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