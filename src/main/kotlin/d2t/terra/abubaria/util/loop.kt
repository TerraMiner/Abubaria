package d2t.terra.abubaria.util

inline fun loopIndicy(start: Int, end: Int, action: (i: Int) -> Unit) {
    var i = start
    while (i <= end) {
        action(i++)
    }
}

inline fun loopWhile(start: Int, end: Int, action: (i: Int) -> Unit) = loopIndicy(start,end-1,action)