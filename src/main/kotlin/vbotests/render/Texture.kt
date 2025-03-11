package vbotests.render

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.STBImage.stbi_image_free
import org.lwjgl.stb.STBImage.stbi_load
import vbotests.game.cleaner
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.nio.file.Paths
import kotlin.io.path.exists

class Texture(fileName: String) {

    var id = 0
    private val _width: IntBuffer = BufferUtils.createIntBuffer(1)
    private val _height: IntBuffer = BufferUtils.createIntBuffer(1)

    val width: Int
    val height: Int

    private val comp: IntBuffer = BufferUtils.createIntBuffer(1)
    private val data: ByteBuffer = stbi_load(fileName, _width, _height, comp, 4)!!

    init {
        id = glGenTextures()
        width = _width.get()
        height = _height.get()
        glBindTexture(GL_TEXTURE_2D, id)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST.toFloat())
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST.toFloat())
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data)
        glBindTexture(GL_TEXTURE_2D, 0)
        stbi_image_free(data)

        cleaner.register(this) {
            glDeleteTextures(id)
        }
    }

    fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun bind() {
        glBindTexture(GL_TEXTURE_2D, id)
    }

    companion object {
        val imageTextures = HashMap<String, Texture?>()

        fun getTexture(path: String) = imageTextures.getOrPut(path) {
            val res = javaClass.getClassLoader().getResource(path.replace("res/",""))
//            println(res?.toURI()?.let { Paths.get(it).exists() } != true)
            if (path == "" || res?.toURI()?.let { Paths.get(it).exists() } != true) {
                null
            } else Texture(path)
        }
    }
}