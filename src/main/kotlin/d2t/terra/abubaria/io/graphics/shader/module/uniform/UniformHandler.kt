package d2t.terra.abubaria.io.graphics.shader.module.uniform

interface UniformHandler<T, A> {
    fun createArray(size: Int): A
    fun copyArray(array: A): A
    fun setUniform(location: Int, value: T)
    fun getValue(array: A, index: Int): T
    fun setValue(array: A, index: Int, value: T)
}