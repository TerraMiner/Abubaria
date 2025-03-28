package d2t.terra.abubaria.io.graphics.shader.module

import d2t.terra.abubaria.io.graphics.shader.module.uniform.FloatUniformHandler
import org.lwjgl.opengl.GL20.glUniform1f

class ShaderAnglerModule : ShaderModule<Float, FloatArray>(
    FloatUniformHandler,
    "angle"
) {
    fun setAngle(angle: Float) {
        setValue(0, angle)
    }
}