package d2t.terra.abubaria.io.graphics

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBImage
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
    private val data: ByteBuffer = STBImage.stbi_load(fileName, _width, _height, comp, 4)!!

    init {
        id = GL11.glGenTextures()
        width = _width.get()
        height = _height.get()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id)
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST.toFloat())
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST.toFloat())
        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D,
            0,
            GL11.GL_RGBA,
            width,
            height,
            0,
            GL11.GL_RGBA,
            GL11.GL_UNSIGNED_BYTE,
            data
        )
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
        STBImage.stbi_image_free(data)
    }

    fun unbind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
    }

    fun bind() {
        if (bindedTexture == id) return
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id)
        bindedTexture = id
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