import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.io.File
import javax.imageio.ImageIO


fun scaleImage(original: BufferedImage, width: Int, height: Int): BufferedImage {

    val scaledImage = BufferedImage(width, height, original.type)
    val g2 = scaledImage.createGraphics()
    g2.drawImage(original, 0, 0, width, height, null)
    g2.dispose()

    return scaledImage
}


fun BufferedImage.padTexture(): BufferedImage {
    val oldWidth = this.width
    val oldHeight = this.height
    val newWidth = oldWidth + 2
    val newHeight = oldHeight + 2

    val paddedImg = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
    val g2d = paddedImg.graphics as Graphics2D

    g2d.drawImage(this, null, 1, 1)

    val pixels = (paddedImg.raster.dataBuffer as DataBufferInt).data

    for (x in 1..oldWidth) {
        var pixel = getRGB(x - 1, 0)
        pixels[x] = pixel
        pixels[x + newWidth] = pixel

        pixel = getRGB(x - 1, oldHeight - 1)
        pixels[(newHeight - 1) * newWidth + x] = pixel
        pixels[(newHeight - 2) * newWidth + x] = pixel
    }
    for (y in 1..oldHeight) {
        var pixel = getRGB(0, y - 1)
        pixels[y * newWidth] = pixel
        pixels[(y + 1) * newWidth - 1] = pixel

        pixel = getRGB(oldWidth - 1, y - 1)
        pixels[(y + 1) * newWidth - 2] = pixel
        pixels[(y + 2) * newWidth - 1] = pixel
    }
    pixels[0] = this.getRGB(0, 0)
    pixels[newWidth - 1] = this.getRGB(oldWidth - 1, 0)
    pixels[newWidth * newHeight - 1] = this.getRGB(oldWidth - 1, oldHeight - 1)
    pixels[(newHeight - 1) * newWidth] = this.getRGB(0, oldHeight - 1)

    return paddedImg
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
    private val list = mutableMapOf<String, Long>()
    fun check(info: String) {
        list[info] = System.nanoTime() - startTime
        startTime = System.nanoTime()
    }

    fun debug(string: String) {
        println("===========================")
        list.entries.sortedBy { it.value / 1000000.0 }.reversed().forEach {
            println("§e$string§f:§6 ${it.key} §f-§c ${it.value / 1000000.0}")
        }
    }
}