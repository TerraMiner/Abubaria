package d2t.terra.abubaria.io.graphics

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class Model(texCoords: FloatArray) {

    private var texId = 0
    private var vaoId = 0

    init {
        vaoId = GL30.glGenVertexArrays()
        GL30.glBindVertexArray(vaoId)

        texId = GL15.glGenBuffers()
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texId)

        val texCoordsBuffer = BufferUtils.createFloatBuffer(texCoords.size).put(texCoords).flip()
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texCoordsBuffer, GL15.GL_STATIC_DRAW)
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0)

        GL20.glEnableVertexAttribArray(0)

        GL30.glBindVertexArray(0)
    }

    fun render(primitiveType: Int = GL11.GL_TRIANGLE_FAN, vertexCount: Int = 4) {
        if (cvaoId != vaoId) {
            GL30.glBindVertexArray(vaoId)
            cvaoId = vaoId
        }
        GL11.glDrawArrays(primitiveType, 0, vertexCount)
    }

    companion object {
        var cvaoId = -1
        private val defaultTexCoords = floatArrayOf(
            0f, 1f,
            1f, 1f,
            1f, 0f,
            0f, 0f
        )
        val DEFAULT = Model(defaultTexCoords)
    }
}