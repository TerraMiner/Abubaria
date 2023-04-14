import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.screenHeight2
import d2t.terra.abubaria.GamePanel.screenWidth2
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.world.Block
import d2t.terra.abubaria.world.tile.Material
import java.awt.Color
import java.awt.Graphics2D
import java.awt.MouseInfo
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.floor

class Cursor(private var x: Int,private var y: Int) {
    var leftClick = false
    var rightClick = false
    var midClick = false
    var mouseInWindow = false

    var currentBlock: Block? = null

    private val player = GamePanel.player

    private var image: BufferedImage = UtilityTool.scaleImage(ImageIO.read(File("res/cursor/cursor.png")),30,30)

    val world = GamePanel.world

    private fun getGamePositionX(): Int {
        val tileSize = tileSize.toDouble()
        var x = (x.toDouble() / tileSize) - (Camera.screenX.toDouble() / tileSize) + (player.location.x / tileSize)

        if (Camera.screenX > player.location.x) x = this.x.toDouble() / tileSize

        if (screenWidth2 - Camera.screenX > world.worldWidth - player.location.x)
            x = world.worldWidth.toDouble() / tileSize - (screenWidth2.toDouble() / tileSize - this.x.toDouble() / tileSize)

        return floor(x).toInt()
    }

    private fun getGamePositionY(): Int {
        val tileSize = tileSize.toDouble()
        var y = y.toDouble() / tileSize - Camera.screenY.toDouble() / tileSize + player.location.y / tileSize

        if (Camera.screenY > player.location.y) y = this.y.toDouble() / tileSize

        if (screenHeight2 - Camera.screenY > world.worldHeight - player.location.y)
            y = world.worldHeight.toDouble() / tileSize - (screenHeight2.toDouble() / tileSize - this.y.toDouble() / tileSize)

        return floor(y).toInt()
    }

    private fun getBlockPosition(): Block? {
        if (!mouseInWindow) return null

        return world.getBlockAt(getGamePositionX(), getGamePositionY())
    }

    fun draw(g2: Graphics2D) {
        if (mouseInWindow) {
            getBlockPosition().also { block ->
                currentBlock = block

                val prevColor = g2.color
                g2.color = Color.GREEN
                if (Client.debugMode) g2.drawString(block?.material?.name ?: "null", x, y)
                g2.color = prevColor

                currentBlock?.hitBox?.color = Color.GREEN
            }
            g2.drawImage(image, x, y, null)
        }
    }

    fun update() {
        val info = MouseInfo.getPointerInfo()

        if (mouseInWindow) {
            info.location.apply {
                this@Cursor.x = x - window.rootPane.x - window.locationOnScreen.x /*- 9*/
                this@Cursor.y = y - window.rootPane.y - window.locationOnScreen.y /*- 32*/
            }
        }

        if (leftClick) currentBlock?.material = Material.AIR
        if (rightClick) currentBlock?.material = Material.GRASS
        if (midClick) currentBlock?.material = Material.STONE
    }
}