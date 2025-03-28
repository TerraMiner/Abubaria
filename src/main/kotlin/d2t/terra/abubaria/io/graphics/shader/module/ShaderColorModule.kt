package d2t.terra.abubaria.io.graphics.shader.module

import d2t.terra.abubaria.io.graphics.shader.module.uniform.FloatUniformHandler
import org.lwjgl.opengl.GL20.glUniform1f

class ShaderColorModule : ShaderModule<Float, FloatArray>(
    FloatUniformHandler,
    "color_r",
    "color_g",
    "color_b",
    "color_a"
) {
    fun setColorRed(value: Float) {
        setValue(0,value)
    }

    fun setColorGreen(value: Float) {
        setValue(1,value)
    }

    fun setColorBlue(value: Float) {
        setValue(2,value)
    }

    fun setColorAlpha(value: Float) {
        setValue(3,value)
    }

    fun setColor(r: Float, g: Float, b: Float, a: Float) {
        setColorRed(r)
        setColorGreen(g)
        setColorBlue(b)
        setColorAlpha(a)
    }
}