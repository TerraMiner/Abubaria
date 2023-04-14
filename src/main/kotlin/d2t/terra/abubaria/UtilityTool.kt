import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt


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


class LagDebugger {
    private var startTime = System.currentTimeMillis()
    private val list = mutableMapOf<String, Long>()
    fun check(info: String) {
        list[info] = System.currentTimeMillis() - startTime
        startTime = System.currentTimeMillis()
    }

    fun debug(string: String) {
        println("===========================")
        list.entries.sortedBy { it.value }.reversed().forEach {
            println("§e$string§f:§6 ${it.key} §f-§c ${it.value}")
        }
    }
}