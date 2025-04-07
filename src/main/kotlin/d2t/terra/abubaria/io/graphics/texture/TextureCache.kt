package d2t.terra.abubaria.io.graphics.texture

import d2t.terra.abubaria.io.graphics.render.Layer
import d2t.terra.abubaria.io.graphics.render.Renderer.FLOATS_PER_SPRITE
import d2t.terra.abubaria.io.graphics.render.Renderer.MAX_SPRITES
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer

class TextureCache(val texture: Texture) {
    var currentLayer: Layer? = null
    lateinit var currentBuffer: FloatBuffer

    fun update(layer: Layer) {
//        if (currentTextureId != texture.id) {
            if (currentLayer !== layer) {
                currentLayer = layer
                currentBuffer = layer.textures.getOrPut(texture.id) {
                    BufferUtils.createFloatBuffer(MAX_SPRITES * FLOATS_PER_SPRITE)
                }
                layerGetCalls++
            }
//            currentTextureId = texture.id
//        }
    }

    companion object {
        var layerGetCalls = 0
//        private var currentTextureId: Int = -1
    }
}