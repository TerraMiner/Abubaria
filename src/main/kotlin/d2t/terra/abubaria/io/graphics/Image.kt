package d2t.terra.abubaria.io.graphics

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBImage
import java.nio.ByteBuffer

class Image(byteBuffer: ByteBuffer? = null, texturePath: String? = null) {

    private var imageWidth = IntArray(1)
    private var imageHeight = IntArray(1)

    val textureId = GL11.glGenTextures()

    var image =
        if (byteBuffer === null) STBImage.stbi_load(
            texturePath ?: "",
            imageWidth,
            imageHeight,
            IntArray(1),
            STBImage.STBI_rgb_alpha
        )
        else byteBuffer

    var width = imageWidth[0]
    var height = imageHeight[0]

    fun subImage(x: Int, y: Int, width: Int, height: Int): Image {
        val subImageSizeInBytes = width * height * 4
        val subImageByteBuffer = BufferUtils.createByteBuffer(subImageSizeInBytes)

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS)

        GL11.glMatrixMode(GL11.GL_TEXTURE)

        for (row in y until y + height) {
            for (col in x until x + width) {

                val pixelIndex = (row * this.width + col) * 4

                subImageByteBuffer.put(this.image!![pixelIndex])
                subImageByteBuffer.put(this.image!![pixelIndex + 1])
                subImageByteBuffer.put(this.image!![pixelIndex + 2])
                subImageByteBuffer.put(this.image!![pixelIndex + 3])

            }
        }

        subImageByteBuffer.flip()

        val subImage = Image(subImageByteBuffer).apply {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)

            setupTextureParameters()

            GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA,
                width,
                height,
                0,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                image
            )

            GL11.glMatrixMode(GL11.GL_MODELVIEW)
            GL11.glPopMatrix()

            GL11.glPopAttrib()
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
            GL11.glPopMatrix()
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)

            setupTextureParameters()

            GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA,
                width,
                height,
                0,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                image
            )
        }
    }

    init {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)
        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D,
            0,
            GL11.GL_RGBA,
            width,
            height,
            0,
            GL11.GL_RGBA,
            GL11.GL_UNSIGNED_BYTE,
            image
        )
        drawTexture(textureId, -10, -10, 0, 0)
    }
}