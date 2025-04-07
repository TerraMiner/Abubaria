package d2t.terra.abubaria.io.graphics.render

import d2t.terra.abubaria.DebugDisplay
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.io.fonts.*
import d2t.terra.abubaria.io.graphics.Color
import d2t.terra.abubaria.io.graphics.texture.Model
import d2t.terra.abubaria.io.graphics.texture.Texture
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.io.graphics.shader.BatchShader
import d2t.terra.abubaria.io.graphics.texture.TextureCache
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20.glVertexAttribPointer
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL31.glDrawArraysInstanced
import org.lwjgl.opengl.GL33.glVertexAttribDivisor
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.math.floor
import kotlin.math.round

//todo Пофиксить артефакты
object Renderer {

    const val MAX_SPRITES = 64000

    // x, y, width, height, uvX, uvY, uvMX, uvMY, zIndex, r, g, b, a, thickness, rotation, ignoreZoom, renderType
    const val FLOATS_PER_SPRITE = 17
    const val FLOATS_PER_SPRITE_BYTES = FLOATS_PER_SPRITE * Float.SIZE_BYTES

    const val BUFFER_SIZE = MAX_SPRITES * FLOATS_PER_SPRITE
    const val BUFFER_SIZE_BYTES = BUFFER_SIZE * Float.SIZE_BYTES

    val shader = BatchShader()
    private var vao = glGenVertexArrays()
    private var vbo = glGenBuffers()

    private var cameraScale = 1.0f

    init {
        glBindVertexArray(vao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)

        glBufferData(GL_ARRAY_BUFFER, BUFFER_SIZE_BYTES.toLong(), GL_STREAM_DRAW)

        glVertexAttribPointer(0, 4, GL_FLOAT, false, FLOATS_PER_SPRITE_BYTES, 0L)
        glEnableVertexAttribArray(0)

        glVertexAttribPointer(1, 4, GL_FLOAT, false, FLOATS_PER_SPRITE_BYTES, 4 * Float.SIZE_BYTES.toLong())
        glEnableVertexAttribArray(1)

        glVertexAttribPointer(2, 1, GL_FLOAT, false, FLOATS_PER_SPRITE_BYTES, 8 * Float.SIZE_BYTES.toLong())
        glEnableVertexAttribArray(2)

        glVertexAttribPointer(3, 4, GL_FLOAT, false, FLOATS_PER_SPRITE_BYTES, 9 * Float.SIZE_BYTES.toLong())
        glEnableVertexAttribArray(3)

        glVertexAttribPointer(4, 2, GL_FLOAT, false, FLOATS_PER_SPRITE_BYTES, 13 * Float.SIZE_BYTES.toLong())
        glEnableVertexAttribArray(4)

        glVertexAttribPointer(5, 1, GL_FLOAT, false, FLOATS_PER_SPRITE_BYTES, 15 * Float.SIZE_BYTES.toLong())
        glEnableVertexAttribArray(5)

        glVertexAttribPointer(6, 1, GL_FLOAT, false, FLOATS_PER_SPRITE_BYTES, 16 * Float.SIZE_BYTES.toLong())
        glEnableVertexAttribArray(6)

        glVertexAttribDivisor(0, 1)
        glVertexAttribDivisor(1, 1)
        glVertexAttribDivisor(2, 1)
        glVertexAttribDivisor(3, 1)
        glVertexAttribDivisor(4, 1)
        glVertexAttribDivisor(5, 1)
        glVertexAttribDivisor(6, 1)

        glBindBuffer(GL_ARRAY_BUFFER, 0)

        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)

        glEnable(GL_BLEND)
//        glDisable(GL_MULTISAMPLE)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    }

    fun render(
        texture: Texture,
        model: Model,
        x: Float, y: Float,
        width: Float, height: Float,
        layer: Layer, dim: RenderDimension,
        angle: Float = 0f,
        color: Color = Color.WHITE,
        thickness: Float = 1f,
        ignoreZoom: Boolean = true
    ) {
        render(
            texture,
            dim.offset().x + x, dim.offset().y + y, width, height,
            model.uvX, model.uvY, model.uvMX, model.uvMY,
            layer,
            color.r, color.g, color.b, color.a,
            thickness, angle,
            ignoreZoom,
            RenderType.TEXTURE
        )
    }

    fun renderText(
        text: String,
        x: Float, y: Float,
        fontSize: Int,
        layer: Layer,
        dim: RenderDimension,
        horAlign: TextHorAligment = TextHorAligment.LEFT,
        horPos: TextHorPosition = TextHorPosition.LEFT,
        verAlign: TextVerAlignment = TextVerAlignment.BOTTOM,
        verPos: TextVerPosition = TextVerPosition.BOTTOM,
        color: Color = Color.WHITE,
        angle: Float = 0f, thickness: Float = 1f,
        ignoreZoom: Boolean = true,
        font: CFont = GamePanel.font
    ) {
        val scale = fontSize.toFloat() / font.size
        if (text.isBlank()) return
        val lines = text.split("\n")

        val maxWidth = lines.maxOf { getTextWidth(it, font) * scale }
        val maxHeight = lines.size * font.size * scale

        val startX = x - round(maxWidth * horPos.offset)
        val startY = y - round(maxHeight * verPos.offset)

        lines.forEachIndexed { index, line ->
            val lineWidth = getTextWidth(line, font) * scale
            val alignedX = startX + round((maxWidth - lineWidth) * horAlign.offset)
            val alignedY = startY + round(index * font.size * scale * verAlign.offset)
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
                    layer = layer,
                    dim = dim,
                    thickness = thickness,
                    ignoreZoom = ignoreZoom
                )
                cursorX += charInfo.advanceWidth * scale
            }
        }
    }

    fun renderRectangle(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        layer: Layer,
        dim: RenderDimension,
        color: Color = Color.WHITE,
        angle: Float = 0f,
        thickness: Float = 1f,
        ignoreZoom: Boolean = true
    ) {
        render(
            Texture.whiteTexture,
            dim.offset().x + x, dim.offset().y + y, width, height,
            0f, 0f, 1f, 1f,
            layer,
            color.r, color.g, color.b, color.a,
            thickness, angle,
            ignoreZoom,
            RenderType.HOLL_RECT
        )
    }

    fun renderFilledRectangle(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        layer: Layer,
        dim: RenderDimension,
        color: Color = Color.WHITE,
        angle: Float = 0f,
        ignoreZoom: Boolean = true
    ) {
        render(
            Texture.whiteTexture,
            dim.offset().x + x, dim.offset().y + y, width, height,
            0f, 0f, 1f, 1f,
            layer,
            color.r, color.g, color.b, color.a,
            1f, angle,
            ignoreZoom,
            RenderType.FILL_RECT
        )
    }

    fun renderLine(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        layer: Layer,
        dim: RenderDimension,
        thickness: Float = 1f,
        color: Color = Color.WHITE,
        ignoreZoom: Boolean = true
    ) {
        render(
            Texture.whiteTexture,
            dim.offset().x + x1, dim.offset().y + y1,
            dim.offset().x + x2, dim.offset().y + y2,
            0f, 0f, 1f, 1f,
            layer,
            color.r, color.g, color.b, color.a,
            thickness, 0f,
            ignoreZoom,
            RenderType.LINE
        )
    }

    private fun render(
        texture: Texture,
        x: Float, y: Float, width: Float, height: Float,
        uvX: Float, uvY: Float, uvMX: Float, uvMY: Float,
        layer: Layer,
        r: Float, g: Float, b: Float, a: Float,
        thickness: Float, rotation: Float,
        ignoreZoom: Boolean,
        renderType: RenderType
    ) {
        texture.cache.update(layer)
        val buffer = texture.cache.currentBuffer
        buffer.put(floor(x))
        buffer.put(floor(y))
        buffer.put(floor(width))
        buffer.put(floor(height))

        buffer.put(uvX)
        buffer.put(uvY)
        buffer.put(uvMX)
        buffer.put(uvMY)

        buffer.put(layer.value)

        buffer.put(r)
        buffer.put(g)
        buffer.put(b)
        buffer.put(a)

        buffer.put(thickness)
        buffer.put(rotation)
        buffer.put(if (ignoreZoom) 1f else 0f)
        buffer.put(renderType.value)
    }

    fun render() {
        shader.bind()
        glBindVertexArray(vao)

        glClear(GL_DEPTH_BUFFER_BIT)

        for (layer in Layer.entries) {
            renderLayer(layer)
        }
        DebugDisplay.bgp = TextureCache.layerGetCalls
        TextureCache.layerGetCalls = 0
    }

    private fun renderLayer(layer: Layer) {
        for ((textureId, buffer) in layer.textures) {
            Texture.bind(textureId)
            val batchCount = buffer.position() / 17
            if (batchCount > 0) {
                buffer.flip()
                glBindBuffer(GL_ARRAY_BUFFER, vbo)
                glBufferSubData(GL_ARRAY_BUFFER, 0, buffer)
                glDrawArraysInstanced(GL_TRIANGLES, 0, 6, batchCount)
                buffer.clear()
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

    private fun Boolean.toFloat() = if (this) 1f else 0f
}
