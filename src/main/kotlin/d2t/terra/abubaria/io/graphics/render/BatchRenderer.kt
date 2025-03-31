package d2t.terra.abubaria.io.graphics.render

import d2t.terra.abubaria.io.graphics.Color
import d2t.terra.abubaria.io.graphics.shader.BatchShader
import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.Window
import org.joml.Matrix4f
import org.joml.Vector4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.GL_DEPTH_TEST
import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL11.GL_LEQUAL
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import org.lwjgl.opengl.GL11.glDrawArrays
import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.GL_STREAM_DRAW
import org.lwjgl.opengl.GL15.glBindBuffer
import org.lwjgl.opengl.GL15.glBufferData
import org.lwjgl.opengl.GL15.glBufferSubData
import org.lwjgl.opengl.GL15.glGenBuffers
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20.glVertexAttribPointer
import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glGenVertexArrays
import org.lwjgl.opengl.GL30.glEnable
import org.lwjgl.opengl.GL30.glDepthFunc
import org.lwjgl.opengl.GL30.glClear
import org.lwjgl.opengl.GL30.GL_DEPTH_BUFFER_BIT

abstract class BatchRenderer {
    val shader = BatchShader()
    protected var currentIndex = 0
    protected var vao = 0
    protected var vbo = 0
    protected var vertices = FloatArray(0)
    abstract val viewMatrix: Vector4f

    protected val batchSessions = mutableListOf<BatchSession>()
    protected val floatBuffer = BufferUtils.createFloatBuffer(MAX_SPRITES * VERTEX_SIZE)

    init {
        vao = glGenVertexArrays()
        vbo = glGenBuffers()
        vertices = FloatArray(BUFFER_SIZE)

        glBindVertexArray(vao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)

        glBufferData(GL_ARRAY_BUFFER, BUFFER_SIZE_BYTES, GL_STREAM_DRAW)

        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)

        glVertexAttribPointer(0, 4, GL_FLOAT, false, VERTEX_STRIDE, 0L)
        glVertexAttribPointer(1, 4, GL_FLOAT, false, VERTEX_STRIDE, 4L * Float.SIZE_BYTES)
        glVertexAttribPointer(2, 1, GL_FLOAT, false, VERTEX_STRIDE, 8L * Float.SIZE_BYTES)

        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
        glEnableVertexAttribArray(2)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
    }

    protected fun addVertexData(element: BatchElement) {
        val x = element.x
        val y = element.y
        val w = element.width
        val h = element.height
        val texCoords = element.model.texCoords

        addVertex(x, y, texCoords[6], texCoords[7], element)
        addVertex(x, y + h, texCoords[0], texCoords[1], element)
        addVertex(x + w, y + h, texCoords[2], texCoords[3], element)

        addVertex(x, y, texCoords[6], texCoords[7], element)
        addVertex(x + w, y + h, texCoords[2], texCoords[3], element)
        addVertex(x + w, y, texCoords[4], texCoords[5], element)
    }

    private fun addVertex(x: Float, y: Float, u: Float, v: Float, element: BatchElement) {
        vertices[currentIndex++] = x
        vertices[currentIndex++] = y
        vertices[currentIndex++] = u
        vertices[currentIndex++] = v
        vertices[currentIndex++] = element.color.r
        vertices[currentIndex++] = element.color.g
        vertices[currentIndex++] = element.color.b
        vertices[currentIndex++] = element.color.a
        vertices[currentIndex++] = element.type.ordinal.toFloat()
    }

    fun session(action: BatchSession.() -> Unit) {
        val session = BatchSession()
        action(session)
        batchSessions.add(session)
    }

    fun begin() {
        currentIndex = 0
    }

    open fun flush() {
        if (batchSessions.isEmpty()) return

        shader.bind()
        shader.setView(viewMatrix.x, viewMatrix.y)
        glBindVertexArray(vao)

        glClear(GL_DEPTH_BUFFER_BIT)

        batchSessions.forEach { session ->
            if (session.elements.isEmpty()) return@forEach
            session.elements.forEach { (texture, elements) ->
                texture.bind()
                currentIndex = 0

                elements.forEach(::addVertexData)

                floatBuffer.clear()
                floatBuffer.put(vertices, 0, currentIndex)
                floatBuffer.flip()

                glBindBuffer(GL_ARRAY_BUFFER, vbo)
                glBufferSubData(GL_ARRAY_BUFFER, 0, floatBuffer)

                val vertexCount = currentIndex / FLOATS_PER_VERTEX
                glDrawArrays(GL_TRIANGLES, 0, vertexCount)
            }
        }

        batchSessions.clear()
    }

    fun end() {
        flush()
        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    fun register() {
        shader.register()
    }

    fun updateProjection() {
        shader.bind()
        shader.setProjection(Matrix4f().setOrtho2D(0f, Window.width.toFloat(), Window.height.toFloat(), 0f))
    }

    data class BatchElement(
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float,
        val model: Model,
        val angle: Float = 0f,
        val color: Color = Color.WHITE,
        val type: RenderType = RenderType.TEXTURE,
    )

    enum class RenderType {
        TEXTURE,
        GEOMETRY,
        PARTICLE
    }

    companion object {
        const val MAX_SPRITES = 64000
        const val VERTICES_PER_SPRITE = 6
        const val FLOATS_PER_VERTEX = 9
        const val VERTEX_SIZE = VERTICES_PER_SPRITE * FLOATS_PER_VERTEX
        const val VERTEX_STRIDE = FLOATS_PER_VERTEX * Float.SIZE_BYTES

        const val BUFFER_SIZE = MAX_SPRITES * VERTEX_SIZE
        const val BUFFER_SIZE_BYTES = (BUFFER_SIZE * Float.SIZE_BYTES).toLong()
    }
}