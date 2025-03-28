package d2t.terra.abubaria.io.graphics.render

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.io.fonts.CFont
import d2t.terra.abubaria.io.fonts.TextHorAligment
import d2t.terra.abubaria.io.fonts.TextHorPosition
import d2t.terra.abubaria.io.fonts.TextVerAlignment
import d2t.terra.abubaria.io.fonts.TextVerPosition
import d2t.terra.abubaria.io.graphics.Color
import d2t.terra.abubaria.io.graphics.shader.TextureShader
import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.Texture
import kotlin.math.floor
import kotlin.math.round

abstract class TextureRenderer : Renderer<Texture, TextureShader>() {
    override fun render(element: Texture, model: Model, x: Float, y: Float, width: Float, height: Float) {
        shader.bind()
        element.bind()
        shader.transform.setTranslationAndScale(view.add(x,y,0f,0f).also { it.w = width; it.z = height })
        model.render()
    }

    open fun render(element: Texture, model: Model, x: Float, y: Float, width: Float, height: Float, angle: Float) {
        if (angle == 0f) {
            render(element,model,x,y,width,height)
        } else {
            shader.performSnapshot(shader.angler) {
                element.bind()
                shader.transform.setTranslationAndScale(view.add(x, y, 0f, 0f).also { it.w = width; it.z = height })
                shader.angler.setAngle(angle)
                model.render()
            }
        }
    }

    open fun renderText(
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
                shader.colorPalette.setColor(color.r, color.g, color.b, color.a)
                charInfo.render(this, font, cursorX, alignedY, scale)
                cursorX += charInfo.advanceWidth * scale
            }
        }
    }

    fun getTextWidth(text: String, font: CFont) = text.sumOf { font.getCharacter(it).advanceWidth }
}