package d2t.terra.abubaria.io

import lwjgl.Image
import lwjgl.drawTexture
import lwjgl.toImage
import java.awt.*
import java.awt.font.FontRenderContext
import java.awt.image.BufferedImage
import java.io.File
import kotlin.math.absoluteValue

private val chars = mutableMapOf<Char, Image>()

fun String.draw(step: Int, x: Int, y: Int, width: Int, height: Int) {

    forEachIndexed { index, c ->
        val posX = x + index * step
        drawTexture(chars[c]?.textureId, posX, y, width, height)
    }

}

object TextRender {
    private val ge: GraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
    private var font: Font

    init {
        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, File("fonts/Comic Sans MS.ttf")))
        font = Font("Comic Sans MS", Font.PLAIN, 16)

        for (i in 32..255) {
            if (i == 127) continue

            val c = i.toChar()
            val charImage = createCharImage(font, c)

            chars[c] = charImage.toImage()
        }

    }

    private fun createCharImage(font: Font, c: Char): BufferedImage {
        val image1 = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        val frc: FontRenderContext = (image1.createGraphics() as Graphics2D).fontRenderContext
        val glyphVector = font.createGlyphVector(frc, c.toString())

        val rect = glyphVector.visualBounds
        val ascent = font.size
        val width = rect.width.toInt() + 2
        val height = rect.height.toInt() + ascent + 2

        val baseline = rect.y.absoluteValue.toInt() + ascent

        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g = image.createGraphics()

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)

        g.font = font
        g.paint = Color.WHITE
        g.drawString(c.toString(), 0, baseline)
        g.dispose()

        return image
    }

//    private fun createCharImage(font: Font, c: Char): BufferedImage {
//
//        val image1 = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
//        val fontGraph = image1.createGraphics()
//        fontGraph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
//        fontGraph.font = font
//        val metrics = fontGraph.fontMetrics
//
//        val charWidth: Int = metrics.charWidth(c)
//        val charHeight: Int = metrics.height
//
//        val image = BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB)
//        val g = image.createGraphics()
//
//        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
//
//        g.font = font
//        g.paint = Color.WHITE
//        g.drawString("$c", 0, metrics.ascent)
//        g.dispose()
//        return image
//    }


}
