package vbotests.render

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20.*
import vbotests.game.cleaner
import java.nio.FloatBuffer

class Model(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
) {

    companion object {
        val indices = intArrayOf(
            0, 1, 2,
            2, 3, 0,
        )
        val texCoords = floatArrayOf(
            0f, 1f,
            1f, 1f,
            1f, 0f,
            0f, 0f,
        )
    }

    private val vertices = floatArrayOf(
        x, y + height, 0f,//topleft
        x + width, y + height, 0f,//topright
        x + width, y, 0f,//bottomright
        x, y, 0f,//bottomleft
    )

    private val screenCoords = createBuffer(vertices)
    private val paletteCoords = createBuffer(texCoords)

    private var drawCount = indices.size
    private var vboId = glGenBuffers()
    private var textureId = glGenBuffers()
    private var iId = glGenBuffers()

    init {
        rebindScreenCoords()
        rebindTextureCoords()

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iId)

        val buffer = BufferUtils.createIntBuffer(indices.size)
        buffer.put(indices)
        buffer.flip()

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW)

        glBindBuffer(GL_ARRAY_BUFFER, 0)

        cleaner.register(this) {
            GL15.glDeleteBuffers(textureId)
            GL15.glDeleteBuffers(vboId)
            GL15.glDeleteBuffers(iId)
        }
    }

    private fun rebindScreenCoords() {
        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, screenCoords, GL_DYNAMIC_DRAW)
    }

    private fun rebindTextureCoords() {
        glBindBuffer(GL_ARRAY_BUFFER, textureId)
        glBufferData(GL_ARRAY_BUFFER, paletteCoords, GL_DYNAMIC_DRAW)
    }

    private fun createBuffer(data: FloatArray): FloatBuffer {
        val buffer = BufferUtils.createFloatBuffer(data.size)
        buffer.put(data)
        buffer.flip()
        return buffer
    }

    init {
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)

        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)

        glBindBuffer(GL_ARRAY_BUFFER, textureId)
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iId)
        GL11.glDrawElements(GL_TRIANGLES, drawCount, GL_UNSIGNED_INT, 0)

//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
//        glBindBuffer(GL_ARRAY_BUFFER, 0)

//        glDisableVertexAttribArray(0)
//        glDisableVertexAttribArray(1)
    }

    fun render() {
//        glEnableVertexAttribArray(0)
//        glEnableVertexAttribArray(1)

//        glBindBuffer(GL_ARRAY_BUFFER, vboId)
//        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)

//        glBindBuffer(GL_ARRAY_BUFFER, textureId)
//        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0)

//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iId)
        GL11.glDrawElements(GL_TRIANGLES, drawCount, GL_UNSIGNED_INT, 0)

//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
//        glBindBuffer(GL_ARRAY_BUFFER, 0)

//        glDisableVertexAttribArray(0)
//        glDisableVertexAttribArray(1)
    }
}