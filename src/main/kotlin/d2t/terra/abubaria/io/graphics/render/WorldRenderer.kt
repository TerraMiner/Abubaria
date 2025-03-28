package d2t.terra.abubaria.io.graphics.render

import d2t.terra.abubaria.world.Camera
import d2t.terra.abubaria.io.graphics.shader.TextureShader
import org.joml.Vector4f

class WorldRenderer : TextureRenderer() {
    override val viewMatrix: Vector4f = Vector4f(0f,0f,1f,1f)
    override val shader = TextureShader("shaders/shader")
    override val view: Vector4f get() = Vector4f(viewMatrix).also {
        it.x = Camera.cameraX
        it.y = Camera.cameraY
    }
}