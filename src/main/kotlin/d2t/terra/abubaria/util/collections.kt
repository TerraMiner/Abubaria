package d2t.terra.abubaria.util

import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

fun <T> concurrentSetOf(vararg objects: T): MutableSet<T> =
    concurrentSetOf<T>().apply { addAll(objects) }

fun <T> concurrentSetOf(): MutableSet<T> =
    Collections.newSetFromMap(ConcurrentHashMap())