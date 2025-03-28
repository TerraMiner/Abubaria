package d2t.terra.abubaria.io.graphics.render

import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.Texture
import d2t.terra.abubaria.io.graphics.shader.GeometryShader
import d2t.terra.abubaria.io.graphics.shader.TextureShader
import d2t.terra.abubaria.io.graphics.shader.module.geometry.ShaderShapeModule
import d2t.terra.abubaria.world.Camera
import org.joml.Vector4f
import org.lwjgl.opengl.GL11

class GeometryWorldRenderer : Renderer<ShaderShapeModule.ShapeType, GeometryShader>() {
    override val viewMatrix: Vector4f = Vector4f(0f,0f,1f,1f)
    override val shader = GeometryShader("shaders/geometry_shader")
    override val view: Vector4f get() = Vector4f(viewMatrix).also {
        it.x = Camera.cameraX
        it.y = Camera.cameraY
    }

    override fun render(element: ShaderShapeModule.ShapeType, model: Model, x: Float, y: Float, width: Float, height: Float) {
        shader.bind()
        shader.shape.setShape(element)
        shader.transform.setTranslationAndScale(view.add(x,y,0f,0f).also { it.w = width; it.z = height })
        val primitiveType = when (element) {
            ShaderShapeModule.ShapeType.LINE -> GL11.GL_LINES
            ShaderShapeModule.ShapeType.RECT_HOLLOW -> GL11.GL_LINE_LOOP
            else -> GL11.GL_TRIANGLE_FAN
        }
        model.render(primitiveType)
    }
}