package d2t.terra.abubaria.io.graphics.render

import d2t.terra.abubaria.io.graphics.shader.TextureShader
import org.joml.Vector4f

class UIRenderer : TextureRenderer() {
    override val viewMatrix: Vector4f = Vector4f(0f,0f,1f,1f)
    override val shader: TextureShader = TextureShader("shaders/shader")
}