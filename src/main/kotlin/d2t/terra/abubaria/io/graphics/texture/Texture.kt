package d2t.terra.abubaria.io.graphics.texture

import d2t.terra.abubaria.io.graphics.render.Layer
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import org.lwjgl.stb.STBImage
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import kotlin.collections.set

class Texture {
    var id: Int
    val width: Int
    val height: Int
    val channels: Int
    val cache = TextureCache(this)

    constructor(id: Int) {
        this.id = id
        this.width = 0
        this.height = 0
        this.channels = 4
    }

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

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

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

        // Здесь устанавливаем параметры обёртывания
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

        // Параметры фильтрации (уже есть в вашем коде)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun bind() = bind(id)

    fun unbind() = Companion.unbind()

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

                val tempFile = File.createTempFile("texture", ".png")
                tempFile.deleteOnExit()

                resourceStream.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }

                tempFile.absolutePath
            }.getOrElse { path }

            Texture(texturePath)
        }
    }
}