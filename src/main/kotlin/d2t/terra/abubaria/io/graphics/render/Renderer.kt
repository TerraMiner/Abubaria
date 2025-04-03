package d2t.terra.abubaria.io.graphics.render

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.io.fonts.CFont
import d2t.terra.abubaria.io.fonts.TextHorAligment
import d2t.terra.abubaria.io.fonts.TextHorPosition
import d2t.terra.abubaria.io.fonts.TextVerAlignment
import d2t.terra.abubaria.io.fonts.TextVerPosition
import d2t.terra.abubaria.io.graphics.Color
import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.shader.BatchShader
import d2t.terra.abubaria.io.graphics.Texture
import d2t.terra.abubaria.io.graphics.Window
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.GL_ALWAYS
import org.lwjgl.opengl.GL11.GL_BLEND
import org.lwjgl.opengl.GL11.GL_DEPTH_TEST
import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL11.GL_LEQUAL
import org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA
import org.lwjgl.opengl.GL11.GL_SRC_ALPHA
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import org.lwjgl.opengl.GL11.GL_TRUE
import org.lwjgl.opengl.GL11.glBlendFunc
import org.lwjgl.opengl.GL11.glDepthMask
import org.lwjgl.opengl.GL14.GL_FUNC_ADD
import org.lwjgl.opengl.GL14.glBlendEquation
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
import org.lwjgl.opengl.GL31.glDrawArraysInstanced
import org.lwjgl.opengl.GL33.glVertexAttribDivisor
import kotlin.math.round
//todo Пофиксить артефакты
object Renderer {

    const val MAX_SPRITES = 64000
    const val FLOATS_PER_SPRITE = 17  // x, y, width, height, uvX, uvY, uvMX, uvMY, zIndex, r, g, b, a, thickness, rotation, ignoreCamera, renderType

    const val BUFFER_SIZE = MAX_SPRITES * FLOATS_PER_SPRITE
    const val BUFFER_SIZE_BYTES = BUFFER_SIZE * Float.SIZE_BYTES

    private val shader = BatchShader()
    private var vao = glGenVertexArrays()
    private var vbo = glGenBuffers()

    // Добавляем параметры камеры
    private var cameraScale = 1.0f

    init {
        glBindVertexArray(vao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)

        glBufferData(GL_ARRAY_BUFFER, BUFFER_SIZE_BYTES.toLong(), GL_STREAM_DRAW)

        // 4 флоата для xy координат x, y, width, height
        glVertexAttribPointer(0, 4, GL_FLOAT, false, FLOATS_PER_SPRITE * Float.SIZE_BYTES, 0L)
        glEnableVertexAttribArray(0)

        // 4 флоата для uv координат uvX, uvY, uvMX, uvMY
        glVertexAttribPointer(1, 4, GL_FLOAT, false, FLOATS_PER_SPRITE * Float.SIZE_BYTES, 4 * Float.SIZE_BYTES.toLong())
        glEnableVertexAttribArray(1)

        // 1 флоат для z индекса
        glVertexAttribPointer(2, 1, GL_FLOAT, false, FLOATS_PER_SPRITE * Float.SIZE_BYTES, 8 * Float.SIZE_BYTES.toLong())
        glEnableVertexAttribArray(2)

        // 4 флоата для цвета r, g, b, a
        glVertexAttribPointer(3, 4, GL_FLOAT, false, FLOATS_PER_SPRITE * Float.SIZE_BYTES, 9 * Float.SIZE_BYTES.toLong())
        glEnableVertexAttribArray(3)

        // 2 флоата для трансформации thickness, rotation
        glVertexAttribPointer(4, 2, GL_FLOAT, false, FLOATS_PER_SPRITE * Float.SIZE_BYTES, 13 * Float.SIZE_BYTES.toLong())
        glEnableVertexAttribArray(4)

        // 1 флоат для флага ignoreCamera
        glVertexAttribPointer(5, 1, GL_FLOAT, false, FLOATS_PER_SPRITE * Float.SIZE_BYTES, 15 * Float.SIZE_BYTES.toLong())
        glEnableVertexAttribArray(5)

        // 1 флоат для типа рендеринга
        glVertexAttribPointer(6, 1, GL_FLOAT, false, FLOATS_PER_SPRITE * Float.SIZE_BYTES, 16 * Float.SIZE_BYTES.toLong())
        glEnableVertexAttribArray(6)

        glVertexAttribDivisor(0, 1)
        glVertexAttribDivisor(1, 1)
        glVertexAttribDivisor(2, 1)
        glVertexAttribDivisor(3, 1)
        glVertexAttribDivisor(4, 1)
        glVertexAttribDivisor(5, 1)
        glVertexAttribDivisor(6, 1)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)

        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    }

    fun render(
        texture: Texture,
        model: Model,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        angle: Float = 0f,
        color: Color = Color.WHITE,
        zIndex: Float,
        dim: RenderDimension,
        thickness: Float = 1f,
        ignoreCamera: Boolean = true
    ) {
        texture.addToBatch(
            dim.offset().x + x,
            dim.offset().y + y,
            width,
            height,
            model.uvX,
            model.uvY,
            model.uvMX,
            model.uvMY,
            zIndex,
            color.r,
            color.g,
            color.b,
            color.a,
            thickness,
            angle,
            if (ignoreCamera) 1f else 0f,
            0f
        )
    }

    fun renderText(
        text: String,
        x: Float,
        y: Float,
        fontSize: Int,
        font: CFont = GamePanel.font,
        textHorAligment: TextHorAligment = TextHorAligment.LEFT,
        textHorPosition: TextHorPosition = TextHorPosition.LEFT,
        textVerAlignment: TextVerAlignment = TextVerAlignment.BOTTOM,
        textVerPosition: TextVerPosition = TextVerPosition.BOTTOM,
        color: Color = Color.WHITE,
        zIndex: Float,
        dim: RenderDimension,
        angle: Float = 0f,
        scaleX: Float = 1f,
        ignoreCamera: Boolean = true
    ) {
        val scale = fontSize.toFloat() / font.size
        if (text.isBlank()) return
        val lines = text.split("\n")

        val maxWidth = lines.maxOf { getTextWidth(it, font) * scale }
        val maxHeight = lines.size * font.size * scale

        val startX = x - round(maxWidth * textHorPosition.offset)
        val startY = y - round(maxHeight * textVerPosition.offset)

        lines.forEachIndexed { index, line ->
            val lineWidth = getTextWidth(line, font) * scale
            val alignedX = startX + round((maxWidth - lineWidth) * textHorAligment.offset)
            val alignedY = startY + round(index * font.size * scale * textVerAlignment.offset)
            var cursorX = alignedX

            for (char in line) {
                val charInfo = font.getCharacter(char)
                render(
                    font.imageFont, charInfo.model,
                    cursorX + (charInfo.xOffset * scale),
                    alignedY - (charInfo.yOffset * scale),
                    charInfo.inAtlasWidth * scale,
                    charInfo.inAtlasHeight * scale,
                    angle = angle,
                    color = color,
                    zIndex = zIndex,
                    dim = dim,
                    thickness = scaleX,
                    ignoreCamera = ignoreCamera
                )
                cursorX += charInfo.advanceWidth * scale
            }
        }
    }

    fun render() {
        shader.bind()
        glBindVertexArray(vao)

        glClear(GL_DEPTH_BUFFER_BIT)
        Texture.cache.values.forEach { texture ->
            val group = texture.batchGroup
            if (group.count > 0) {
                texture.bind()

                group.vertexData.flip()

                glBindBuffer(GL_ARRAY_BUFFER, vbo)
                glBufferSubData(GL_ARRAY_BUFFER, 0, group.vertexData)

                glDrawArraysInstanced(GL_TRIANGLES, 0, 6, group.count)

                texture.clearBatch()
            }
        }
    }

    fun register() {
        shader.register()
    }

    fun updateProjection() {
        shader.bind()
        shader.setProjection(Matrix4f().setOrtho2D(0f, Window.width.toFloat(), Window.height.toFloat(), 0f))
        shader.setCameraCenter(Window.centerX, Window.centerY)
    }

    private fun getTextWidth(text: String, font: CFont) = text.sumOf { font.getCharacter(it).advanceWidth }

    fun setCameraScale(scale: Float) {
        cameraScale = scale
        shader.bind()
        shader.setCameraScale(cameraScale, cameraScale)
    }

    fun setCameraCenter(x: Float, y: Float) {
        shader.bind()
        shader.setCameraCenter(x, y)
    }

    fun renderRectangle(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: Color = Color.WHITE,
        zIndex: Float,
        dim: RenderDimension,
        angle: Float = 0f,
        thickness: Float = 1f,
        ignoreCamera: Boolean = true
    ) {
        Texture.whiteTexture.addToBatch(
            dim.offset().x + x,
            dim.offset().y + y,
            width,
            height,
            0f, 0f, 1f, 1f,
            zIndex,
            color.r,
            color.g,
            color.b,
            color.a,
            thickness,
            angle,
            if (ignoreCamera) 1f else 0f,
            2f
        )
    }

    fun renderFilledRectangle(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: Color = Color.WHITE,
        zIndex: Float,
        dim: RenderDimension,
        angle: Float = 0f,
        ignoreCamera: Boolean = true
    ) {
        Texture.whiteTexture.addToBatch(
            dim.offset().x + x,
            dim.offset().y + y,
            width,
            height,
            0f, 0f, 1f, 1f,
            zIndex,
            color.r,
            color.g,
            color.b,
            color.a,
            1f,
            angle,
            if (ignoreCamera) 1f else 0f,
            1f
        )
    }

    fun renderLine(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        thickness: Float = 1f,
        color: Color = Color.WHITE,
        zIndex: Float,
        dim: RenderDimension,
        ignoreCamera: Boolean = true
    ) {
        val offsetX = dim.offset().x
        val offsetY = dim.offset().y
        
        Texture.whiteTexture.addToBatch(
            offsetX + x1,
            offsetY + y1,
            offsetX + x2,
            offsetY + y2,
            0f, 0f, 1f, 1f,
            zIndex,
            color.r,
            color.g,
            color.b,
            color.a,
            thickness,
            0f,
            if (ignoreCamera) 1f else 0f,
            3f
        )
    }
}