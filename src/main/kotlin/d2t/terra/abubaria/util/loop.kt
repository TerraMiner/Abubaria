package d2t.terra.abubaria.util

inline fun loopIndicy(start: Int, end: Int, action: (i: Int) -> Unit) {
    var i = start
    while (i <= end) {
        action(i++)
    }
}

inline fun loopWhile(start: Int, end: Int, action: (i: Int) -> Unit) = loopIndicy(start, end - 1, action)

inline fun any(iterations: Int, from: Int = 0, action: (Int) -> Boolean): Boolean {
    loopWhile(from, iterations) {
        if (action(it)) return true
    }
    return false
}

inline fun none(iterations: Int, from: Int = 0, action: (Int) -> Boolean): Boolean {
    loopWhile(from, iterations) {
        if (!action(it)) return true
    }
    return false
}

fun <T> T.for2d(
    dxmin: Int,
    dxmax: Int,
    dymin: Int,
    dymax: Int,
    action: T.(x: Int, y: Int) -> Unit
): T {
    var dx: Int = dxmin
    var dy: Int = dymin
    while (dx <= dxmax) {
        while (dy <= dymax) {
            action(dx, dy)
            ++dy
        }
        dy = dymin
        ++dx
    }

    return this
}

fun <T> T.for2d(
    dxmin: Int,
    dxmax: Int,
    dymin: Int,
    dymax: Int,
    action: T.(x: Int) -> T.(y: Int) -> Unit
): T {
    for (x in dxmin..dxmax) {
        val actionForX = this.action(x)
        for (y in dymin..dymax) {
            actionForX(y)
        }
    }
    return this
}