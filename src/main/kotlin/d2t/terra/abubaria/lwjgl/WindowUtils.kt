package d2t.terra.abubaria.lwjgl

import d2t.terra.abubaria.GamePanel
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import org.lwjgl.stb.STBImage.*
import java.awt.Color
import java.awt.image.BufferedImage
import java.nio.ByteBuffer


fun loadImage(texturePath: String) = Image(null, texturePath)

class Image(byteBuffer: ByteBuffer? = null, texturePath: String? = null) {

    private var imageWidth = IntArray(1)
    private var imageHeight = IntArray(1)

    val textureId = glGenTextures()

    var image = if (byteBuffer === null) stbi_load(texturePath ?: "", imageWidth, imageHeight, IntArray(1), STBI_rgb_alpha)
    else byteBuffer

    var width = imageWidth[0]
    var height = imageHeight[0]

    fun subImage(x: Int, y: Int, width: Int, height: Int): Image {
        val subImageSizeInBytes = width * height * 4
        val subImageByteBuffer = BufferUtils.createByteBuffer(subImageSizeInBytes)

        glPushAttrib(GL_ALL_ATTRIB_BITS)

        glMatrixMode(GL_TEXTURE)

        for (row in y until y + height) {
            for (col in x until x + width) {
                kotlin.runCatching {
                    val pixelIndex = (row * this.width + col) * 4

                    subImageByteBuffer.put(this.image!![pixelIndex])
                    subImageByteBuffer.put(this.image!![pixelIndex + 1])
                    subImageByteBuffer.put(this.image!![pixelIndex + 2])
                    subImageByteBuffer.put(this.image!![pixelIndex + 3])
                }.getOrElse {
                    println("ERROR x:$x, y:$y, col:$col, row:$row, width:$width, height:$height")
                    it.printStackTrace()
                    return Image()
                }
            }
        }

        subImageByteBuffer.flip()

        val subImage = Image(subImageByteBuffer).apply {
            glBindTexture(GL_TEXTURE_2D, textureId)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image)

            glMatrixMode(GL_MODELVIEW)
            glPopMatrix()

            glPopAttrib()
        }

        return subImage
    }


    fun subTextImage(x: Int, y: Int, width: Int, height: Int): Image {
        val subImageSizeInBytes = width * height * 4
        val subImageByteBuffer = BufferUtils.createByteBuffer(subImageSizeInBytes)

        for (row in y - height until y) {
            for (col in x until x + width) {
                val pixelIndex = ((row) * this.width + col) * 4

                subImageByteBuffer.put(this.image!![pixelIndex + 0])
                subImageByteBuffer.put(this.image!![pixelIndex + 1])
                subImageByteBuffer.put(this.image!![pixelIndex + 2])
                subImageByteBuffer.put(this.image!![pixelIndex + 3])
            }
        }

        subImageByteBuffer.flip()

        return Image(subImageByteBuffer).apply {
            glPopMatrix()
            glBindTexture(GL_TEXTURE_2D, textureId)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image)
        }
    }

    init {
        glBindTexture(GL_TEXTURE_2D, textureId)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image)
        drawTexture(textureId,-10,-10,0,0)
    }
}


fun drawRect(x: Int, y: Int, width: Int, height: Int, lineWidth: Float = 1f, color: Color = Color.BLACK) {
    glPushAttrib(GL_CURRENT_BIT or GL_ENABLE_BIT or GL_TRANSFORM_BIT)
    glDisable(GL_TEXTURE_2D)
    glColor3f(color.red / 255f, color.green / 255f, color.blue / 255f)
    glLineWidth(lineWidth)
    glBegin(GL_LINE_LOOP)
    glVertex2f(x.toFloat(), y.toFloat())
    glVertex2f(x.toFloat() + width, y.toFloat())
    glVertex2f(x.toFloat() + width, y.toFloat() + height)
    glVertex2f(x.toFloat()-1f, y.toFloat() + height)
    glEnd()
    glPopAttrib()
}

fun drawString(string: String, x: Int, y: Int, sizeMod: Int, color: Color = Color.WHITE) {
    GamePanel.font.apply {
        var xMod = x
        string.forEach {
            val char = getCharacter(it)

            glBindTexture(GL_TEXTURE_2D, char.textureId)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

            val widthAdjusted = char.width / sizeMod
            val heightAdjusted = char.height / sizeMod
            glBegin(GL_QUADS)
            glColor4f(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)

            glTexCoord2f(0.0f, 0.0f)
            glVertex2i(xMod, y - fontMetrics.descent)

            glTexCoord2f(0.0f, 1.0f)
            glVertex2i(xMod, y + heightAdjusted - fontMetrics.descent)

            glTexCoord2f(1.0f, 1.0f)
            glVertex2i(xMod + widthAdjusted, y + heightAdjusted - fontMetrics.descent)

            glTexCoord2f(1.0f, 0.0f)
            glVertex2i(xMod + widthAdjusted, y - fontMetrics.descent)

            glEnd()

            xMod += widthAdjusted
        }
    }
}

fun drawTexture(textureId: Int?, x: Int, y: Int, width: Int, height: Int, color: Color = Color.WHITE) {

    if (textureId === null) return

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)


    glBindTexture(GL_TEXTURE_2D, textureId)
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    glColor4f(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)
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
