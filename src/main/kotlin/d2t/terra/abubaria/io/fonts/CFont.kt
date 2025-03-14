package d2t.terra.abubaria.io.fonts

import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.Texture
import d2t.terra.abubaria.util.loopIndicy
import d2t.terra.abubaria.util.loopWhile
import java.awt.*
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.sqrt


class CFont(private val path: String, private val name: String, val size: Int) {
    private var width: Int = 0
    private var height: Int = 0
    private var lineHeight: Int = 0
    private val characterMap: MutableMap<Int, CharInfo> = mutableMapOf()
    private val ge: GraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
    private val imageFile = "fonts/${name}_atlas.png"
    lateinit var imageFont: Texture
    lateinit var fontMetrics: FontMetrics

    init {
        generateBitmap()
    }

    private fun generateBitmap() {
        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, File(path)))
        val font = Font(name, Font.BOLD, size)

        var img = BufferedImage(1, 1, TYPE_INT_ARGB)
        var g2d = img.createGraphics()
        g2d.font = font

        val metrics = g2d.fontMetrics
        fontMetrics = metrics

        val estimatedWidth = (sqrt(font.numGlyphs.toDouble()) * font.size.toDouble() / 2.5).toInt() + 1
        width = 0
        height = metrics.height
        lineHeight = metrics.height

        val allowedCharsRegex = "[]A-Za-z0-9!\"#$%&'()*+,-./:;<=>?@\\[\\\\^_`{|}~А-Яа-яЁё ]".toRegex()

        var x = 0
        var y = metrics.ascent

        loopWhile(0, font.numGlyphs) { i ->
            if (font.canDisplay(i) && allowedCharsRegex.matches(Char(i).toString())) {
                val ch = Character.toChars(i)[0]
                val charWidth = metrics.charWidth(ch)
                val charHeight = metrics.ascent + metrics.descent

                val info = CharInfo(x, y - metrics.ascent, charWidth, charHeight)
                characterMap[i] = info
                width = max(x + charWidth, width)

                x += charWidth + 10

                if (x > estimatedWidth) {
                    x = 0
                    y += metrics.height
                    height += metrics.height
                }
            }
        }


        height += metrics.height
        buildCharInfoModels()
        g2d.dispose()

        if (!File(imageFile).exists()) {
            kotlin.runCatching {
                img = BufferedImage(width, height, TYPE_INT_ARGB)
                g2d = img.createGraphics()
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                g2d.font = font
                g2d.color = Color.WHITE

                loopWhile(0, font.numGlyphs) { i ->
                    if (font.canDisplay(i)) {
                        val info = characterMap[i] ?: return@loopWhile
                        g2d.drawString("${Char(i)}", info.sourceX, info.sourceY + metrics.ascent)
                    }
                }

                val file = File(imageFile)
                ImageIO.write(img, "png", file)
                g2d.dispose()
            }.getOrElse {
                it.printStackTrace()
            }
        }

        imageFont = Texture(imageFile)
    }

    private fun buildCharInfoModels() {
        characterMap.values.forEach { info ->
            info.buildModel(width, height)
        }
    }

    fun getCharacter(codepoint: Char): CharInfo {
        return characterMap[codepoint.code] ?: characterMap['?'.code]!!
    }
}