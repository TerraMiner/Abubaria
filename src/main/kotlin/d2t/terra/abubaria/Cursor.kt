package d2t.terra.abubaria

import d2t.terra.abubaria.GamePanel.screenHeight2
import d2t.terra.abubaria.GamePanel.screenWidth2
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.entity.player.inventory.Item
import d2t.terra.abubaria.entity.player.inventory.maxStackSize
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.Block
import d2t.terra.abubaria.world.tile.Material
import lwjgl.drawTexture
import lwjgl.loadImage
import readImage
import scaleImage
import java.awt.Color
import java.awt.Graphics2D
import java.awt.MouseInfo
import java.awt.image.BufferedImage
import kotlin.math.floor

class Cursor(private var x: Int, private var y: Int) {

    var leftPress = false
    var rightPress = false
    var midPress = false

    var leftClick = false
    var rightClick = false
    var midClick = false

    var mouseInWindow = false
    var cursorText = ""
    var mouseOnHud = false

    var cursorItem: Item = Item()
    var currentBlock: Block? = null

    private var image =/*: BufferedImage = scaleImage(readImage(*/loadImage("cursor/cursor.png")/*), 30, 30)*/

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

    fun draw(location: Location) {
        if (mouseInWindow) {
            getBlockPosition().also { block ->
                currentBlock = block

//                val prevColor = g2.color
//                g2.color = Color.GREEN

//                g2.drawString(cursorText, x, y - 3)

                if (Client.debugMode && !mouseOnHud) {

                    block?.apply {
                        val screenX = Camera.worldScreenPosX(x * tileSize, location)
                        val screenY = Camera.worldScreenPosY(y * tileSize, location)

//                        g2.drawRect(
//                            screenX,
//                            screenY + type.state.offset,
//                            hitBox.width.toInt(),
//                            hitBox.height.toInt()
//                        )

//                        g2.drawString("$x $y", screenX, screenY + block.type.state.offset)
                    }
                }

//                g2.color = prevColor
            }

            drawTexture(image, x, y, 30, 30)
            drawTexture(cursorItem.type.texture, x + 5, y + 15, 15, 15)

//            g2.drawImage(image, x, y, null)
//            g2.drawImage(cursorItem.type.texture, x + 5, y + 15, 15, 15, null)
        }
    }

    fun update() {
        val info = MouseInfo.getPointerInfo()

        if (mouseInWindow) {
            info.location.apply {
                this@Cursor.x = x - GamePanel.screenPosX /*- 9*/
                this@Cursor.y = y - GamePanel.screenPosY /*- 32*/
            }

            val bound = ClientPlayer.inventory.inventoryBound
            mouseOnHud = (x >= bound.x && y >= bound.y && x < bound.x + bound.width && y < bound.y + bound.height)

            ClientPlayer.inventory.updateMouseSlot(x, y)

            if (mouseOnHud) handleMouseHud()
            else handleMouseWorld()
            endMousehandle()
        }

    }

    fun endMousehandle() {
        leftClick = false
        rightClick = false
        midClick = false
    }

    fun handleMouseHud() {

        val hoveredItem = ClientPlayer.inventory.getItemMouse(x, y)

        cursorText = if (hoveredItem?.type !== Material.AIR)
            (hoveredItem?.run { "$display $amount" }) ?: ""
        else ""

        if (ClientPlayer.inventory.opened) {
            if (leftClick) {

                ClientPlayer.inventory.setItemMouse(x, y, cursorItem)

                cursorItem = hoveredItem ?: run {
                    println("ERROR")
                    return
                }
            }
        } else {
            if (leftClick) ClientPlayer.selectedHotBar = ClientPlayer.inventory.hoveredSlot.first
        }
    }

    fun handleMouseWorld() {

        cursorText = if (currentBlock?.type !== Material.AIR)
            currentBlock?.type?.display ?: ""
        else ""

        if (!leftPress && !rightPress && !midPress) return

        currentBlock?.apply {

            val hotBarItem = ClientPlayer.run {
                inventory.getItem(selectedHotBar, 0)
            }

            when {
                leftPress -> {
                    if (cursorItem.type === Material.AIR && hotBarItem?.type === Material.AIR) {
                        destroy()
                    } else if (!this.hitBox.clone.transform(1.0, 1.0, -1.0, -1.0).intersects(ClientPlayer.hitBox))
                        type = if (cursorItem.type !== Material.AIR) cursorItem.type
                        else hotBarItem?.type ?: Material.AIR
                }

                rightPress -> {
                    //Drop item
                }

                midPress -> {
                    cursorItem = Item(type, maxStackSize)
                }
            }
        }
    }
}