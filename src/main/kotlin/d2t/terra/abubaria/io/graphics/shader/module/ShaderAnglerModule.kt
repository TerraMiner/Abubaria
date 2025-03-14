package d2t.terra.abubaria.io.graphics.shader.module

import org.lwjgl.opengl.GL20.glUniform1f

class ShaderAnglerModule : ShaderModule("angle") {
    fun setAngle(angle: Float) {
        val index = 0
        if (fieldStatesData[index] == angle) return
        glUniform1f(addressesData[index], angle)
        fieldStatesData[index] = angle
    }
}