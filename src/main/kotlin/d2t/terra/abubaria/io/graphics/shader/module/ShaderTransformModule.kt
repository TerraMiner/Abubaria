package d2t.terra.abubaria.io.graphics.shader.module

import org.joml.Vector4f
import org.lwjgl.opengl.GL20.glUniform1f

class ShaderTransformModule : ShaderModule(
    "translateX",
    "translateY",
    "scaleX",
    "scaleY"
){
    fun setTranslateX(value: Float) {
        val index = 0
        if (fieldStatesData[index] == value) return
        glUniform1f(addressesData[index], value)
        fieldStatesData[index] = value
    }

    fun setTranslateY(value: Float) {
        val index = 1
        if (fieldStatesData[index] == value) return
        glUniform1f(addressesData[index], value)
        fieldStatesData[index] = value
    }

    fun setScaleX(value: Float) {
        val index = 2
        if (fieldStatesData[index] == value) return
        glUniform1f(addressesData[index], value)
        fieldStatesData[index] = value
    }

    fun setScaleY(value: Float) {
        val index = 3
        if (fieldStatesData[index] == value) return
        glUniform1f(addressesData[index], value)
        fieldStatesData[index] = value
    }

    fun setTranslationAndScale(vec: Vector4f) {
        setTranslateX(vec.x)
        setTranslateY(vec.y)
        setScaleX(vec.w)
        setScaleY(vec.z)
    }
}