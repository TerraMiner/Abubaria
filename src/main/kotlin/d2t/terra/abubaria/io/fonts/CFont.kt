package d2t.terra.abubaria.io.fonts

import d2t.terra.abubaria.io.graphics.Image
import d2t.terra.abubaria.io.graphics.loadImage
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
    private val imageFile = "fonts/${name}_map.png"
    private lateinit var imageFont: Image
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

        val estimatedWidth = (sqrt(font.numGlyphs.toDouble()) * font.size.toDouble()).toInt() + 1
        width = 0
        height = metrics.height
        lineHeight = metrics.height

        var x = 0
        val mod = 1.4f
        var y = (metrics.height * mod).toInt()

        for (i in 0 until font.numGlyphs) {
            if (font.canDisplay(i)) {

                val info = CharInfo(x, y, metrics.charWidth(i), metrics.height)

                characterMap[i] = info
                width = max(x + metrics.charWidth(i), width)

                x += info.width + 10

                if (x > estimatedWidth) {
                    x = 0
                    y += (metrics.height * mod).toInt()
                    height += (metrics.height * mod).toInt()
                }
            }
        }

        height += (metrics.height * mod).toInt()

        g2d.dispose()

        img = BufferedImage(width, height, TYPE_INT_ARGB)
        g2d = img.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.font = font
        g2d.color = Color.WHITE

        for (i in 0 until font.numGlyphs) {
            if (font.canDisplay(i)) {
                val info = characterMap[i] ?: continue
                g2d.drawString("${Char(i)}", info.sourceX, info.sourceY)
            }
        }

        if (!File(imageFile).exists()) {
            kotlin.runCatching {
                val file = File(imageFile)
                ImageIO.write(img, "png", file)
            }.getOrElse {
                it.printStackTrace()
            }
        }

        g2d.dispose()

        imageFont = loadImage(imageFile)

        for (i in 0 until font.numGlyphs) {
            if (font.canDisplay(i)) {
                val char = characterMap[i] ?: continue

                char.textureId =
                    imageFont.subTextImage(char.sourceX, char.sourceY + 12, char.width, char.height).textureId

            }
        }
    }

    fun getCharacter(codepoint: Char): CharInfo {
        return characterMap.getOrDefault(codepoint.code, CharInfo(0, 0, 0, 0))
    }

}