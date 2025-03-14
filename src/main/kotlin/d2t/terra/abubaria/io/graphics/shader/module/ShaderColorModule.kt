package d2t.terra.abubaria.io.graphics.shader.module

import org.lwjgl.opengl.GL20.glUniform1f

class ShaderColorModule : ShaderModule(
    "color_r",
    "color_g",
    "color_b",
    "color_a"
) {
    fun setColorRed(value: Float) {
        val index = 0
        if (fieldStatesData[index] == value) return
        glUniform1f(addressesData[index],value)
        fieldStatesData[index] = value
    }

    fun setColorGreen(value: Float) {
        val index = 1
        if (fieldStatesData[index] == value) return
        glUniform1f(addressesData[index],value)
        fieldStatesData[index] = value
    }

    fun setColorBlue(value: Float) {
        val index = 2
        if (fieldStatesData[index] == value) return
        glUniform1f(addressesData[index],value)
        fieldStatesData[index] = value
    }

    fun setColorAlpha(value: Float) {
        val index = 3
        if (fieldStatesData[index] == value) return
        glUniform1f(addressesData[index],value)
        fieldStatesData[index] = value
    }

    fun setColor(r: Float, g: Float, b: Float, a: Float) {
        setColorRed(r)
        setColorGreen(g)
        setColorBlue(b)
        setColorAlpha(a)
    }
}