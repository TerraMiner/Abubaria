package d2t.terra.abubaria.util

fun <T> T.print(addition: String? = null): T {
    return apply { if (addition !== null) println("$addition: $this") else println(this) }
}