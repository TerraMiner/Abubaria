package d2t.terra.abubaria.event

object EventService {
    val handlers = mutableMapOf<Class<*>, MutableList<Any.() -> Unit>>()

    inline fun <reified T:Any> registerHandler(noinline handler: T.() -> Unit) {
        handlers.merge(T::class.java, mutableListOf({ handler(this as T) })){ old, new -> old.apply { addAll(new) } }
    }

    fun <T> launch(value: T) {
        handlers[(value ?: return)::class.java]?.forEach {
            it.invoke(value)
        }
    }
}