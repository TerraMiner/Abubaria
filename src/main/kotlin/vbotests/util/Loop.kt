package vbotests.util

inline fun loopWhile(start: Int, end: Int, action: (i: Int) -> Unit) {
    var i = start
    while (i <= end) {
        action(i++)
    }
}