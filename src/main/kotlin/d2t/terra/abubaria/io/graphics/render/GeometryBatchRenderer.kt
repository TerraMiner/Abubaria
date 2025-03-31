package d2t.terra.abubaria.io.graphics.render

import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.shader.module.geometry.ShaderShapeModule
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import org.lwjgl.opengl.GL11.glDrawArrays
import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.glBindBuffer
import org.lwjgl.opengl.GL15.glBufferSubData
import org.lwjgl.opengl.GL30.glBindVertexArray

//class GeometryBatchRenderer : BatchRenderer() {
//
//    fun drawShape(x: Float, y: Float, width: Float, height: Float,
//                 r: Float = 1f, g: Float = 1f, b: Float = 1f, a: Float = 1f) {
//        batchBuffer.getOrPut(null) { ArrayList() }.add(
//            BatchElement(x, y, width, height, Model.DEFAULT, 0f, r, g, b, a, RenderType.GEOMETRY)
//        )
//    }
//
//    override fun flush() {
//        shader.bind()
//        glBindVertexArray(vao)
//
//        for (elements in batchBuffer.values) {
//            currentIndex = 0
//            for (element in elements) {
//                addVertexData(element)
//            }
//
//            glBindBuffer(GL_ARRAY_BUFFER, vbo)
//            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices.copyOfRange(0, currentIndex))
//            glDrawArrays(GL_TRIANGLES, 0, currentIndex / VERTEX_STRIDE)
//        }
//    }
//}