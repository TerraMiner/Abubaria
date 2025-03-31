package d2t.terra.abubaria.io.graphics

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import org.lwjgl.opengl.GL30.glGenerateMipmap
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.nio.file.Paths
import kotlin.io.path.exists

class Texture {
    var id: Int
    val width: Int
    val height: Int
    val channels: Int

    constructor(fileName: String) {
        val widthBuffer = BufferUtils.createIntBuffer(1)
        val heightBuffer = BufferUtils.createIntBuffer(1)
        val channelsBuffer = BufferUtils.createIntBuffer(1)
        val data = STBImage.stbi_load(fileName, widthBuffer, heightBuffer, channelsBuffer, 0)!!

        id = glGenTextures()
        width = widthBuffer.get()
        height = heightBuffer.get()
        channels = channelsBuffer.get()

        glBindTexture(GL_TEXTURE_2D, id)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        val format = if (channels == 4) GL_RGBA else GL_RGB
        glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, data)
        glBindTexture(GL_TEXTURE_2D, 0)

        STBImage.stbi_image_free(data)
    }

    fun bind() {
        if (bindedTexture == id) return
        glBindTexture(GL_TEXTURE_2D, id)
        bindedTexture = id
    }

    fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
        bindedTexture = 0
    }

    companion object {
        var bindedTexture: Int = -1
        val imageTextures = HashMap<String, Texture?>()

        fun getTexture(path: String) = imageTextures.getOrPut(path) {
            val res = javaClass.getClassLoader().getResource(path.replace("res/",""))
            if (path == "" || res?.toURI()?.let { Paths.get(it).exists() } != true) {
                null
            } else Texture(path)
        }
    }
}