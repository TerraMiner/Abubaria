package d2t.terra.abubaria.io.graphics.shader.module

import d2t.terra.abubaria.util.loopWhile
import org.lwjgl.opengl.GL20.glGetUniformLocation
import org.lwjgl.opengl.GL20.glUniform1f

abstract class ShaderModule(
    vararg val addressNames: String
) {
    protected val addressesData: IntArray = IntArray(addressNames.size)
    protected val fieldStatesData: FloatArray = FloatArray(addressNames.size) { 0f }

    fun allocate(program: Int) {
        addressNames.forEachIndexed { i, name ->
            addressesData[i] = glGetUniformLocation(program, name)
        }
    }

    fun performSnapshot(program: Int, action: () -> Unit) {
        val fieldStateSnapshot = FloatArray(fieldStatesData.size)
        loopWhile(0, addressNames.size) {
            fieldStateSnapshot[it] = fieldStatesData[it]
        }
        action()
        loopWhile(0, addressNames.size) {
            val address = addressesData[it]
            val data = fieldStateSnapshot[it]
            glUniform1f(address,data)
            fieldStatesData[it] = data
        }
    }
}