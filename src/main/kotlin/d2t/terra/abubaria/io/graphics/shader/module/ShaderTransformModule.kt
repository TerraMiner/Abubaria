package d2t.terra.abubaria.io.graphics.shader.module

import d2t.terra.abubaria.io.graphics.shader.module.uniform.FloatUniformHandler
import org.joml.Vector4f
import org.lwjgl.opengl.GL20.glUniform1f

class ShaderTransformModule : ShaderModule<Float, FloatArray>(
    FloatUniformHandler,
    "translateX",
    "translateY",
    "scaleX",
    "scaleY",
){
    fun setTranslateX(value: Float) {
        setValue(0,value)
    }

    fun setTranslateY(value: Float) {
        setValue(1,value)
    }

    fun setScaleX(value: Float) {
        setValue(2,value)
    }

    fun setScaleY(value: Float) {
        setValue(3,value)
    }

    fun setTranslationAndScale(vec: Vector4f) {
        setTranslateX(vec.x)
        setTranslateY(vec.y)
        setScaleX(vec.w)
        setScaleY(vec.z)
    }
}