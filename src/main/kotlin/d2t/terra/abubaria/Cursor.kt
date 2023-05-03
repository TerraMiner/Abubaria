package d2t.terra.abubaria

import d2t.terra.abubaria.GamePanel.screenHeight2
import d2t.terra.abubaria.GamePanel.screenWidth2
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.entity.player.inventory.Item
import d2t.terra.abubaria.io.devices.MouseHandler
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.Block
import d2t.terra.abubaria.world.tile.Material
import lwjgl.drawRect
import lwjgl.drawString
import lwjgl.drawTexture
import lwjgl.loadImage
import java.awt.Color
import kotlin.math.floor

class Cursor(private var x: Int, private var y: Int) {

    var leftPress = false
    var rightPress = false
    var midPress = false

    var leftClick = false
    var rightClick = false
    var midClick = false
    var cursorText = ""
    var mouseOnHud = false

    var cursorItem: Item = Item()
    var cursorItemSlot = -1 to -1

    var currentBlock: Block? = null

    private var image =/*: BufferedImage = scaleImage(readImage(*/loadImage("cursor/cursor.png")/*), 30, 30)*/

    val world = GamePanel.world

    val inventory get() = ClientPlayer.inventory

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
        return world.getBlockAt(getGamePositionX(), getGamePositionY())
    }

    fun draw(location: Location) {

        getBlockPosition().also { block ->
            currentBlock = block


            if (Client.debugMode && !mouseOnHud) {


                block?.apply {
                    val screenX = Camera.worldScreenPosX(x * tileSize, location)
                    val screenY = Camera.worldScreenPosY(y * tileSize, location)

                    drawRect(
                        screenX,
                        screenY + type.state.offset,
                        hitBox.width.toInt(),
                        hitBox.height.toInt(),
                        1f,
                        Color.GREEN
                    )
                    drawString("$x $y", screenX, screenY + block.type.state.offset, 4, Color.GREEN)
                }
            }
        }


        drawTexture(image.textureId, x, y, 30, 30)
        drawTexture(cursorItem.type.texture?.textureId, x + 5, y + 15, 15, 15)
    }

    fun update() {
        MouseHandler.update()

        MouseHandler.apply {
            this@Cursor.x = x.toInt()
            this@Cursor.y = y.toInt()
        }

        val bound = inventory.inventoryBound
        mouseOnHud = (x >= bound.x && y >= bound.y && x < bound.x + bound.width && y < bound.y + bound.height)

        if (!inventory.opened
            && cursorItem.type !== Material.AIR
            && cursorItemSlot.first != -1 && cursorItemSlot.second != -1
        ) {
            if (inventory.items[cursorItemSlot.first][cursorItemSlot.second].type === Material.AIR) {
                inventory.items[cursorItemSlot.first][cursorItemSlot.second] = cursorItem.clone
            } else {
                //drop item
            }
            cursorItem.remove()
            cursorItemSlot = -1 to -1
        }

        inventory.updateMouseSlot(x, y)

        if (mouseOnHud) handleMouseHud()
        else handleMouseWorld()
        endMouseHandle()

    }

    private fun endMouseHandle() {
        leftClick = false
        rightClick = false
        midClick = false
    }

    private fun handleMouseHud() {

        val hoveredItem = inventory.getItemMouse(x, y)

        cursorText = if (hoveredItem?.type !== Material.AIR)
            (hoveredItem?.run { "$display $amount" }) ?: ""
        else ""

        if (inventory.opened) {
            if (leftClick) {

                inventory.setItemFromMouse(x, y, cursorItem)
                cursorItem = hoveredItem ?: return

            }
        } else {
            if (leftClick) inventory.selectedHotBar = inventory.hoveredSlot.first
        }
    }

    private fun handleMouseWorld() {

        cursorText = if (currentBlock?.type !== Material.AIR)
            currentBlock?.type?.display ?: ""
        else ""



        if (!leftPress && !rightPress && !midPress) return

        currentBlock?.apply {

            val hotBarItem = ClientPlayer.run {
                inventory.getItem(inventory.selectedHotBar, 0)
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
                    val item = Item(type, type.maxStackSize)
                    if (inventory.opened) {
                        cursorItem = item
                        cursorItemSlot = inventory.firstEmptySlot()
                    } else {
                        val targetSlot = inventory.findIdentify(item.type).first
                        if (targetSlot == -1) inventory.setItem(inventory.firstEmptySlot(), item)
                        else inventory.selectedHotBar = targetSlot
                    }

                }
            }
        }
    }
}