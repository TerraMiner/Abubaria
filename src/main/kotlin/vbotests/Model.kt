package vbotests

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import java.nio.FloatBuffer

class Model(val vertices: FloatArray, texCoords: FloatArray, indices: IntArray) {
    var drawCount: Int = indices.size
    var vId: Int = glGenBuffers()
    var tId: Int = glGenBuffers()
    var iId = glGenBuffers()

    init {
        glBindBuffer(GL_ARRAY_BUFFER, vId)
        glBufferData(GL_ARRAY_BUFFER, createBuffer(vertices), GL_DYNAMIC_DRAW)

        glBindBuffer(GL_ARRAY_BUFFER,tId)
        glBufferData(GL_ARRAY_BUFFER,createBuffer(texCoords), GL_DYNAMIC_DRAW)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,iId)

        val buffer = BufferUtils.createIntBuffer(indices.size)
        buffer.put(indices)
        buffer.flip()

        glBufferData(GL_ELEMENT_ARRAY_BUFFER,buffer,GL_DYNAMIC_DRAW)

        glBindBuffer(GL_ARRAY_BUFFER,0)
    }

    fun render() {
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)

        glBindBuffer(GL_ARRAY_BUFFER,vId)
        glVertexAttribPointer(0,3, GL_FLOAT,false,0,0)

        glBindBuffer(GL_ARRAY_BUFFER,tId)
        glVertexAttribPointer(1,2, GL_FLOAT,false,0,0)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,iId)
        GL11.glDrawElements(GL_TRIANGLES,drawCount, GL_UNSIGNED_INT,0)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0)
        glBindBuffer(GL_ARRAY_BUFFER,0)

        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
    }

    fun createBuffer(data: FloatArray): FloatBuffer {
        val buffer = BufferUtils.createFloatBuffer(vertices.size)
        buffer.put(data)
        buffer.flip()
        return buffer
    }
}