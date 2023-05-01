package lwjgl

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import org.lwjgl.stb.STBImage.STBI_rgb_alpha
import org.lwjgl.stb.STBImage.stbi_load
import java.awt.Color
import java.awt.image.BufferedImage
import java.nio.ByteBuffer


fun loadImage(texturePath: String) = Image(null, texturePath)

fun BufferedImage.toImage(): Image {
    val pixels = IntArray(width * height)

    getRGB(0, 0, width, height, pixels, 0, width)

    val buffer = BufferUtils.createByteBuffer(width * height * 4)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val pixel = pixels[y * width + x]

            buffer.put((pixel shr 16 and 0xFF).toByte()) // Red component
            buffer.put((pixel shr 8 and 0xFF).toByte())  // Green component
            buffer.put((pixel and 0xFF).toByte())       // Blue component
            buffer.put((pixel shr 24 and 0xFF).toByte()) // Alpha component
        }
    }

    buffer.flip()

    return Image(buffer)
}

class Image(byteBuffer: ByteBuffer? = null, texturePath: String? = null) {

    private var imageWidth = IntArray(1)
    private var imageHeight = IntArray(1)

    var image: ByteBuffer? =
        byteBuffer ?: stbi_load(texturePath!!, imageWidth, imageHeight, IntArray(1), STBI_rgb_alpha)

    var width = imageWidth[0]
    var height = imageHeight[0]
    val textureId = glGenTextures()

    init {
        glBindTexture(GL_TEXTURE_2D, textureId)
//        glTexture
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image)
    }
}

fun drawRect(x: Int, y: Int, width: Int, height: Int, lineWidth: Float = 1f, color: Color = Color.BLACK) {
    glPushAttrib(GL_CURRENT_BIT or GL_ENABLE_BIT or GL_TRANSFORM_BIT)
    glDisable(GL_TEXTURE_2D)
    glColor3f(color.red/255f, color.green/255f, color.blue/255f)
    glLineWidth(lineWidth)
    glBegin(GL_LINE_LOOP)
    glVertex2f(x.toFloat(), y.toFloat())
    glVertex2f(x.toFloat() + width, y.toFloat())
    glVertex2f(x.toFloat() + width, y.toFloat() + height)
    glVertex2f(x.toFloat()-1, y.toFloat() + height)
    glEnd()
    glPopAttrib()
}

fun drawTexture(textureId: Int?, x: Int, y: Int, width: Int, height: Int) {

    if (textureId == null) return

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

    glBindTexture(GL_TEXTURE_2D, textureId)

    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

    glBegin(GL_QUADS)

    glTexCoord2f(0.0f, 1.0f)
    glVertex2i(x, y + height)
    glTexCoord2f(1.0f, 1.0f)
    glVertex2i(x + width, y + height)
    glTexCoord2f(1.0f, 0.0f)
    glVertex2i(x + width, y)
    glTexCoord2f(0.0f, 0.0f)
    glVertex2i(x, y)

    glEnd()
}
