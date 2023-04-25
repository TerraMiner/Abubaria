import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.screenHeight2
import d2t.terra.abubaria.GamePanel.screenWidth2
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.Block
import d2t.terra.abubaria.world.tile.Material
import java.awt.Color
import java.awt.Graphics2D
import java.awt.MouseInfo
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.floor

class Cursor(private var x: Int, private var y: Int) {
    var leftClick = false
    var rightClick = false
    var midClick = false
    var mouseInWindow = false
    var cursorText = ""

    var selectedType: Material = Material.values().random()
    var currentBlock: Block? = null

    private var image: BufferedImage = scaleImage(ImageIO.read(File("res/cursor/cursor.png")), 30, 30)

    val world = GamePanel.world

    private fun getGamePositionX(): Int {
        val tileSize = tileSize.toDouble()
        var x =
            (x.toDouble() / tileSize) - (Camera.screenX.toDouble() / tileSize) + (ClientPlayer.location.x / tileSize)

        if (Camera.screenX > ClientPlayer.location.x) x = this.x.toDouble() / tileSize

        if (screenWidth2 - Camera.screenX > world.worldWidth - ClientPlayer.location.x) x =
            world.worldWidth.toDouble() / tileSize - (screenWidth2.toDouble() / tileSize - this.x.toDouble() / tileSize)

        return floor(x).toInt()
    }

    private fun getGamePositionY(): Int {
        val tileSize = tileSize.toDouble()
        var y = y.toDouble() / tileSize - Camera.screenY.toDouble() / tileSize + ClientPlayer.location.y / tileSize

        if (Camera.screenY > ClientPlayer.location.y) y = this.y.toDouble() / tileSize

        if (screenHeight2 - Camera.screenY > world.worldHeight - ClientPlayer.location.y) y =
            world.worldHeight.toDouble() / tileSize - (screenHeight2.toDouble() / tileSize - this.y.toDouble() / tileSize)

        return floor(y).toInt()
    }

    private fun getBlockPosition(): Block? {
        if (!mouseInWindow) return null

        return world.getBlockAt(getGamePositionX(), getGamePositionY())
    }

    fun draw(g2: Graphics2D, location: Location) {
        if (mouseInWindow) {
            getBlockPosition().also { block ->
                currentBlock = block
                cursorText = block?.material?.name ?: "null"

                val prevColor = g2.color
                g2.color = Color.GREEN

                g2.drawString(cursorText,x,y - 3)

                if (Client.debugMode) {

                    block?.apply {
                        val screenX = Camera.worldScreenPosX(x * tileSize, location)
                        val screenY = Camera.worldScreenPosY(y * tileSize, location)

                        g2.drawRect(
                            screenX,
                            screenY + material.state.offset,
                            hitBox.width.toInt(),
                            hitBox.height.toInt()
                        )

                        g2.drawString("$x $y",screenX,screenY + block.material.state.offset)
                    }

                }

                g2.color = prevColor
            }
            g2.drawImage(image, x, y, null)
            g2.drawImage(selectedType.texture,x+5,y+15,15,15,null)
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

        currentBlock?.apply {
            if (leftClick) material = Material.AIR
            if (rightClick) {
                if (!this.hitBox.intersects(ClientPlayer.hitBox))
                material = selectedType
            }
            if (midClick) selectedType = material
        }
    }
}