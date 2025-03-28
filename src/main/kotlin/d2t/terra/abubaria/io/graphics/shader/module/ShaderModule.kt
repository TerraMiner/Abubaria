package d2t.terra.abubaria.io.graphics.shader.module

import d2t.terra.abubaria.io.graphics.shader.module.uniform.UniformHandler
import org.lwjgl.opengl.GL20.glGetUniformLocation

abstract class ShaderModule<T, A>(
    protected val handler: UniformHandler<T, A>,
    vararg val addressNames: String
) {
    protected val addressesData: IntArray = IntArray(addressNames.size)
    protected val fieldStatesData: A = handler.createArray(addressNames.size)

    fun allocate(program: Int) {
        addressNames.forEachIndexed { i, name ->
            addressesData[i] = glGetUniformLocation(program, name)
        }
    }

    fun performSnapshot(action: () -> Unit) {
        val fieldStateSnapshot = handler.copyArray(fieldStatesData)
        action()
        addressNames.indices.forEach { i ->
            val address = addressesData[i]
            val value = handler.getValue(fieldStateSnapshot, i)
            handler.setUniform(address, value)
            handler.setValue(fieldStatesData, i, value)
        }
    }

    fun setValue(index: Int, value: T) {
        if (getValue(index) == value) return
        handler.setUniform(addressesData[index], value)
        handler.setValue(fieldStatesData, index, value)
    }

    fun getValue(index: Int): T {
        return handler.getValue(fieldStatesData, index)
    }
}