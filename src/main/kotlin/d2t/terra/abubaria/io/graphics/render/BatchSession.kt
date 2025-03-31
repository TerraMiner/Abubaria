package d2t.terra.abubaria.io.graphics.render

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.io.fonts.CFont
import d2t.terra.abubaria.io.fonts.TextHorAligment
import d2t.terra.abubaria.io.fonts.TextHorPosition
import d2t.terra.abubaria.io.fonts.TextVerAlignment
import d2t.terra.abubaria.io.fonts.TextVerPosition
import d2t.terra.abubaria.io.graphics.Color
import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.Texture
import d2t.terra.abubaria.io.graphics.render.BatchRenderer.BatchElement
import d2t.terra.abubaria.io.graphics.render.BatchRenderer.RenderType
import kotlin.math.round

class BatchSession() {//todo переписать, так не годится. Нужно добавить слои рендера. А ещё добавить возможность каждому элементу задавать угол поворота.
    val elements = LinkedHashMap<Texture, MutableList<BatchElement>>()

    fun render(texture: Texture, model: Model, x: Float, y: Float, width: Float, height: Float) {
        draw(texture, model, x, y, width, height)
    }

    fun render(texture: Texture, model: Model, x: Float, y: Float, width: Float, height: Float, angle: Float) {
        draw(texture, model, x, y, width, height, angle)
    }

    fun render(
        texture: Texture, model: Model, x: Float, y: Float, width: Float, height: Float,
        color: Color
    ) {
        draw(texture, model, x, y, width, height, 0f, color)
    }

    fun render(
        texture: Texture, model: Model, x: Float, y: Float, width: Float, height: Float,
        angle: Float, color: Color
    ) {
        draw(texture, model, x, y, width, height, angle, color)
    }

    private fun draw(
        texture: Texture,
        model: Model,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        angle: Float = 0f,
        color: Color = Color.WHITE,
        type: RenderType = RenderType.TEXTURE,
    ) {
        val list = elements.getOrPut(texture) { mutableListOf() }
        list.add(BatchElement(x, y, width, height, model, angle, color, type))
    }

    fun renderText(
        text: String,
        x: Float,
        y: Float,
        scale: Float,
        font: CFont = GamePanel.font,
        textHorAligment: TextHorAligment = TextHorAligment.LEFT,
        textHorPosition: TextHorPosition = TextHorPosition.LEFT,
        textVerAlignment: TextVerAlignment = TextVerAlignment.BOTTOM,
        textVerPosition: TextVerPosition = TextVerPosition.BOTTOM,
        color: Color = Color.WHITE
    ) {
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
                    0f, color
                )
                cursorX += charInfo.advanceWidth * scale
            }
        }
    }

    fun getTextWidth(text: String, font: CFont) = text.sumOf { font.getCharacter(it).advanceWidth }
}