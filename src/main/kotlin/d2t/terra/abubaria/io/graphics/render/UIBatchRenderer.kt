package d2t.terra.abubaria.io.graphics.render

import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.Texture
import org.joml.Vector4f
import kotlin.collections.getOrPut

class UIBatchRenderer : TextureBatchRenderer() {
    override val viewMatrix: Vector4f = Vector4f(0f, 0f, 1f, 1f)
} 