package d2t.terra.abubaria.io.graphics.shader.module.geometry

import d2t.terra.abubaria.io.graphics.shader.module.ShaderModule
import d2t.terra.abubaria.io.graphics.shader.module.uniform.IntUniformHandler

class ShaderShapeModule : ShaderModule<Int, IntArray>(
    IntUniformHandler,
    "shapeType"
) {
    fun setShape(shapeType: ShapeType) {
        setShape(shapeType.ordinal)
    }

    fun setShape(shapeId: Int) {
        setValue(0, shapeId)
    }

    enum class ShapeType {
        LINE, RECT_HOLLOW, RECT_FILLED
    }
}