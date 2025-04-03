package d2t.terra.abubaria.io.graphics

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.STBImage
import java.nio.ByteBuffer
import kotlin.collections.set

class Texture {
    var id: Int
    val width: Int
    val height: Int
    val channels: Int

    val batchGroup by lazy { TextureGroup() }

    private constructor(fileName: String) {
        val widthBuffer = BufferUtils.createIntBuffer(1)
        val heightBuffer = BufferUtils.createIntBuffer(1)
        val channelsBuffer = BufferUtils.createIntBuffer(1)
        val data = STBImage.stbi_load(fileName, widthBuffer, heightBuffer, channelsBuffer, 0)!!

        id = glGenTextures()
        width = widthBuffer.get()
        height = heightBuffer.get()
        channels = channelsBuffer.get()
        amount++

        glBindTexture(GL_TEXTURE_2D, id)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        val format = if (channels == 4) GL_RGBA else GL_RGB
        glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, data)
        glBindTexture(GL_TEXTURE_2D, 0)

        STBImage.stbi_image_free(data)
    }

    constructor(width: Int, height: Int, data: ByteBuffer) {
        id = glGenTextures()
        this.width = width
        this.height = height
        this.channels = 4
        amount++

        glBindTexture(GL_TEXTURE_2D, id)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun bind() = bind(id)

    fun unbind() = Companion.unbind()

    fun addToBatch(
        x: Float, y: Float, width: Float, height: Float,
        uvX: Float, uvY: Float, uvMX: Float, uvMY: Float,
        zIndex: Float,
        r: Float, g: Float, b: Float, a: Float,
        thickness: Float,
        rotation: Float,
        ignoreCamera: Float,
        renderType: Float = 0f,
    ) {
        val group = batchGroup
        val buffer = group.vertexData

        buffer.put(x)
        buffer.put(y)
        buffer.put(width)
        buffer.put(height)

        buffer.put(uvX)
        buffer.put(uvY)
        buffer.put(uvMX)
        buffer.put(uvMY)

        buffer.put(zIndex)

        buffer.put(r)
        buffer.put(g)
        buffer.put(b)
        buffer.put(a)

        buffer.put(thickness)
        buffer.put(rotation)

        buffer.put(ignoreCamera)
        
        buffer.put(renderType)

        group.count++
    }

    fun clearBatch() {
        batchGroup.clear()
    }

    companion object {
        var binded: Int = -1
        var amount: Int = 0
        val cache = HashMap<String, Texture>()

        val whiteTexture: Texture = run {
            val buffer = BufferUtils.createByteBuffer(4)
            buffer.put(255.toByte()).put(255.toByte()).put(255.toByte()).put(255.toByte())
            buffer.flip()
            Texture(1, 1, buffer)
        }

        init {
            cache["noop_pixel"] = whiteTexture
        }

        fun bind(id: Int) {
            if (binded == id) return
            glBindTexture(GL_TEXTURE_2D, id)
            binded = id
        }

        fun unbind() {
            glBindTexture(GL_TEXTURE_2D, 0)
            binded = 0
        }

        fun get(path: String) = cache.getOrPut(path) {
            val texturePath = runCatching {
                val cleanPath = if (path.startsWith("res/")) path.substring(4) else path
                val resourceStream = javaClass.getResourceAsStream("/${cleanPath}")
                    ?: throw IllegalArgumentException("Resource not found: /${cleanPath}")

                val tempFile = java.io.File.createTempFile("texture", ".png")
                tempFile.deleteOnExit()

                resourceStream.use { input ->
                    java.io.FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }

                tempFile.absolutePath
            }.getOrElse { path }

            Texture(texturePath)
        }
    }
}