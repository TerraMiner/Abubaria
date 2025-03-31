package d2t.terra.abubaria.io.fonts

import d2t.terra.abubaria.io.graphics.Texture
import d2t.terra.abubaria.util.getCoords
import java.awt.*
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.sqrt

class CFont(path: String, val size: Int, val fontType: Int = Font.BOLD) {
    var lineHeight: Int = 0
    val characterMap: MutableMap<Int, CharInfo> = mutableMapOf()
    private val ge: GraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
    private val imageFile = "${path}_atlas.png"
    private val atlasSize: Int
    val atlasSquareSize: Int
    val imageFont: Texture
    val fontMetrics: FontMetrics

    init {
        val font = Font.createFont(Font.TRUETYPE_FONT, File(path)).let {
            ge.registerFont(it)
            Font(it.family, fontType, size)
        }

        var img = BufferedImage(1, 1, TYPE_INT_ARGB)
        var g2d = img.createGraphics()
        g2d.font = font

        val metrics = g2d.fontMetrics
        fontMetrics = metrics

        val availableChars = (32..2000).filter(font::canDisplay)

        val frc = g2d.fontRenderContext
        var maxCharSize = ceil(fontMetrics.height * 1.2).toInt()

        for (codepoint in availableChars) {
            val str = String(Character.toChars(codepoint))
            val gv = font.createGlyphVector(frc, str)
            val visualBounds = gv.visualBounds

            val visualHeight = ceil(visualBounds.height).toInt()
            val visualWidth = ceil(visualBounds.width).toInt()

            maxCharSize = max(maxCharSize, max(visualWidth, visualHeight))
        }

        maxCharSize += 4

        atlasSquareSize = ceil(sqrt(availableChars.size.toDouble())).toInt() + 1

        atlasSize = atlasSquareSize * maxCharSize

        lineHeight = metrics.height

        availableChars.forEachIndexed { i, codepoint ->
            val pos = getCoords(i, atlasSquareSize, atlasSquareSize)
            val inAtlasX = maxCharSize * pos.x
            val inAtlasY = maxCharSize * pos.y

            val charWidth = metrics.charWidth(Char(codepoint))
            val charHeight = metrics.ascent + metrics.descent

            val logicalX = inAtlasX + (maxCharSize - charWidth) / 2
            val logicalY = inAtlasY + (maxCharSize - charHeight) / 2

            val xOffset = inAtlasX - logicalX
            val yOffset = inAtlasY - logicalY

            val info = CharInfo(
                inAtlasX = inAtlasX,
                inAtlasY = inAtlasY,
                inAtlasWidth = maxCharSize,
                inAtlasHeight = maxCharSize,
                xOffset = xOffset,
                yOffset = yOffset,
                advanceWidth = max(1, charWidth)
            )
            characterMap[codepoint] = info
        }

        buildCharInfoModels()
        g2d.dispose()

        if (!File(imageFile).exists()) {

        img = BufferedImage(atlasSize, atlasSize, TYPE_INT_ARGB)
        g2d = img.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.font = font

        characterMap.entries.forEachIndexed { index, (codepoint, info) ->
            val pos = getCoords(index, atlasSquareSize, atlasSquareSize)
            val cellX = maxCharSize * pos.x
            val cellY = maxCharSize * pos.y

            val logicalX = cellX + (maxCharSize - metrics.charWidth(Char(codepoint))) / 2
            val logicalY = cellY + (maxCharSize - (metrics.ascent + metrics.descent)) / 2

            g2d.color = Color.WHITE
            g2d.drawString("${Char(codepoint)}", logicalX, logicalY + metrics.ascent)
        }

        val file = File(imageFile)
        ImageIO.write(img, "png", file)
        g2d.dispose()

        }

        imageFont = Texture(imageFile)
    }

    private fun buildCharInfoModels() {
        characterMap.values.forEach { info ->
            info.buildModel(atlasSize, atlasSize)
        }
    }

    fun getCharacter(codepoint: Char): CharInfo {
        return characterMap[codepoint.code] ?: characterMap['?'.code]!!
    }
}