package d2t.terra.abubaria.io.graphics.render

import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.io.graphics.shader.Shader
import org.joml.Matrix4f
import org.joml.Vector4f
import d2t.terra.abubaria.io.graphics.Model

abstract class Renderer<V, S : Shader> {
    abstract val viewMatrix: Vector4f
    abstract val shader: S
    abstract fun render(element: V, model: Model, x: Float, y: Float, width: Float, height: Float)

    var projectionMatrix: Matrix4f = Matrix4f().setOrtho2D(0f, Window.width.toFloat(), Window.height.toFloat(), 0f)
    open val view get() = Vector4f(viewMatrix)

    fun updateProjection() {
        projectionMatrix = Matrix4f().setOrtho2D(0f, Window.width.toFloat(), Window.height.toFloat(), 0f)
        shader.bind()
        shader.setProjection(projectionMatrix)
    }

    fun register() {
        shader.register()
        shader.setProjection(projectionMatrix)
    }
}