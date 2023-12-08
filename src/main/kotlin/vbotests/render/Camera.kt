package vbotests.render

import org.joml.Matrix4f
import org.joml.Vector3f

class Camera(
    val width: Int,
    val height: Int
) {
    val position = Vector3f(0f)
    private val projection = Matrix4f().setOrtho2D(0f, width.toFloat(), height.toFloat(), 0f)

    fun addPosition(position: Vector3f) {
        this.position.add(position)
    }

    fun getProjection(): Matrix4f {
        return projection.translate(position, Matrix4f())
    }
}