package d2t.terra.abubaria.io.graphics.render

import d2t.terra.abubaria.world.Camera
import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.Texture
import d2t.terra.abubaria.io.graphics.shader.ParticleShader
import d2t.terra.abubaria.io.graphics.shader.TextureShader
import org.joml.Vector4f

class ParticleRenderer : Renderer<Texture, ParticleShader>() {
    override val viewMatrix: Vector4f = Vector4f(0f, 0f, 1f, 1f)
    override val shader = ParticleShader("shaders/particle_shader")
    override val view: Vector4f get() = Vector4f(viewMatrix).also {
            it.x = Camera.cameraX
            it.y = Camera.cameraY
        }

    private var currentTime = 0f

    override fun render(element: Texture, model: Model, x: Float, y: Float, width: Float, height: Float) {
        shader.bind()
        element.bind()

        shader.setTime(currentTime)
        shader.setGridSize(4f, 4f)
        shader.setLifeSpan(1.0f)

        shader.transform.setTranslationAndScale(view.add(x.toFloat(), y.toFloat(), 0f, 0f).also {
            it.w = width.toFloat()
            it.z = height.toFloat()
        })

        model.render()
    }

    fun update(deltaTime: Float) {
        currentTime += deltaTime
    }
}