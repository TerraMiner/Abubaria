package vbotests

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.STBImage.stbi_image_free
import org.lwjgl.stb.STBImage.stbi_load
import java.nio.ByteBuffer
import java.nio.IntBuffer

class Texture(fileName: String) {

    var id = 0
    val width: IntBuffer = BufferUtils.createIntBuffer(1)
    val height: IntBuffer = BufferUtils.createIntBuffer(1)

    var WIDTH = 0
    var HEIGHT = 0

    val comp: IntBuffer = BufferUtils.createIntBuffer(1)
    val data: ByteBuffer = stbi_load(fileName, width, height, comp, 4)!!

    init {
        id = glGenTextures()
        this.WIDTH = width.get()
        this.HEIGHT = height.get()
        glBindTexture(GL_TEXTURE_2D, id)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST.toFloat())
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST.toFloat())
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.WIDTH, this.HEIGHT, 0, GL_RGBA, GL_UNSIGNED_BYTE, data)
        glBindTexture(GL_TEXTURE_2D, 0)
        stbi_image_free(data)
    }


    fun bind() {
        glBindTexture(GL_TEXTURE_2D, id)
    }

    fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)

    }
}