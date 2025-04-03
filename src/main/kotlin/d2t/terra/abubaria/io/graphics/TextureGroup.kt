package d2t.terra.abubaria.io.graphics

import org.lwjgl.BufferUtils
import d2t.terra.abubaria.io.graphics.render.Renderer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class TextureGroup {
    val vertexData: FloatBuffer = BufferUtils.createFloatBuffer(Renderer.BUFFER_SIZE)
    var count: Int = 0
    
    fun clear() {
        vertexData.clear()
        count = 0
    }
} 