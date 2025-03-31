package d2t.terra.abubaria.io.graphics.render

import d2t.terra.abubaria.world.Camera
import org.joml.Vector4f

class WorldBatchRenderer : TextureBatchRenderer() {
    override val viewMatrix: Vector4f = Vector4f(0f, 0f, 1f, 1f)
    
    override fun flush() {
        viewMatrix.x = Camera.cameraX
        viewMatrix.y = Camera.cameraY
        super.flush()
    }
} 