import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL32.*
import org.lwjgl.stb.*
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO

fun scaleImage(original: BufferedImage, width: Int, height: Int): BufferedImage {

    val scaledImage = BufferedImage(width, height, original.type)
    val g2 = scaledImage.createGraphics()
    g2.drawImage(original, 0, 0, width, height, null)
    g2.dispose()

    return scaledImage
}


fun BufferedImage.negative(): BufferedImage {
    val negativeImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

    for (x in 0 until width) {
        for (y in 0 until height) {
            val color = Color(getRGB(x, y), true)
            val negativeColor = Color(255 - color.red, 255 - color.green, 255 - color.blue, color.alpha)
            negativeImage.setRGB(x, y, negativeColor.rgb)
        }
    }
    return negativeImage
}

fun readImage(path: String) = ImageIO.read(File(path))

class LagDebugger {
    private var startTime = System.nanoTime()
    private val list = mutableMapOf<Int, Long>()
    var enabled = true
    fun check(id: Int) {
        if (!enabled) return
        list[id] = System.nanoTime() - startTime
        startTime = System.nanoTime()
    }

    fun debug(string: String) {
        if (!enabled) return
        println("===========================")
        list.entries.sortedBy { it.value / 1000000.0 }.reversed().forEach {
            println("$string: ${it.key} - ${it.value / 1000000.0}")
        }
    }
}
