package d2t.terra.abubaria.io.graphics.shader.module.uniform

import org.lwjgl.opengl.GL20.glUniform1f

object FloatUniformHandler : UniformHandler<Float, FloatArray> {
    override fun createArray(size: Int): FloatArray = FloatArray(size)

    override fun copyArray(array: FloatArray): FloatArray = array.copyOf()

    override fun setUniform(location: Int, value: Float) {
        glUniform1f(location, value)
    }

    override fun getValue(array: FloatArray, index: Int): Float = array[index]

    override fun setValue(array: FloatArray, index: Int, value: Float) {
        array[index] = value
    }
}