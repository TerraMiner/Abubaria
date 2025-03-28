package d2t.terra.abubaria.io.graphics.shader.module.uniform

import org.lwjgl.opengl.GL20.glUniform1i

object IntUniformHandler : UniformHandler<Int, IntArray> {
    override fun createArray(size: Int): IntArray = IntArray(size)

    override fun copyArray(array: IntArray): IntArray = array.copyOf()

    override fun setUniform(location: Int, value: Int) {
        glUniform1i(location, value)
    }

    override fun getValue(array: IntArray, index: Int): Int = array[index]

    override fun setValue(array: IntArray, index: Int, value: Int) {
        array[index] = value
    }
}