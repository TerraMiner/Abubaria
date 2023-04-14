import com.badlogic.gdx.graphics.Texture
import java.awt.image.BufferedImage

object UtilityTool {
    fun scaleImage(original: BufferedImage, width: Int, height: Int): BufferedImage {

        val scaledImage = BufferedImage(width,height,original.type)
        val g2 = scaledImage.createGraphics()
        g2.drawImage(original,0,0,width,height,null)
        g2.dispose()

        return scaledImage
    }
}

class LagDebugger {
    private var startTime = System.currentTimeMillis()
    private val list = mutableMapOf<String,Long>()
    fun check(info: String) {
        list[info] = System.currentTimeMillis() - startTime
        startTime = System.currentTimeMillis()
    }

    fun debug(string: String){
        println("===========================")
        list.entries.sortedBy { it.value }.reversed().forEach {
            println("§e$string§f:§6 ${it.key} §f-§c ${it.value}")
        }
    }
}