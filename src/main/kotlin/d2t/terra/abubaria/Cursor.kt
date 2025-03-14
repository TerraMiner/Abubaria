package d2t.terra.abubaria

import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.GamePanel.tileSizeF
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.inventory.Item
import d2t.terra.abubaria.io.devices.MouseHandler
import d2t.terra.abubaria.io.graphics.Color
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.io.graphics.render.RendererManager
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.block.Block
import d2t.terra.abubaria.world.material.Material
import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.Texture
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

    private var texture = Texture("cursor/cursor.png")

    val world = GamePanel.world

    val inventory get() = ClientPlayer.inventory

    private fun getGamePositionX(): Int {
        val tileSize = tileSize.toDouble()
        var x = (x.toDouble() / tileSize) - (Window.centerX / tileSize) + (ClientPlayer.location.x / tileSize)

        if (Window.centerX > ClientPlayer.location.x) x = this.x.toDouble() / tileSize

        if (Window.width - Window.centerX > world.width - ClientPlayer.location.x) x =
            world.width.toDouble() / tileSize - (Window.width.toDouble() / tileSize - this.x.toDouble() / tileSize)

        return floor(x).toInt()
    }

    private fun getGamePositionY(): Int {
        val tileSize = tileSize.toDouble()
        var y = y.toDouble() / tileSize - Window.centerY.toDouble() / tileSize + ClientPlayer.location.y / tileSize

        if (Window.centerY > ClientPlayer.location.y) y = this.y.toDouble() / tileSize

        if (Window.height - Window.centerY > world.height - ClientPlayer.location.y) y =
            world.height.toDouble() / tileSize - (Window.height.toDouble() / tileSize - this.y.toDouble() / tileSize)

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
                    val offset = (tileSize * type.state.offset).toInt()

                    RendererManager.UIRenderer.renderText("$x $y", screenX, screenY + offset, .3f, color = Color.GREEN)

                }
            }
        }

        RendererManager.UIRenderer.render(texture, Model.DEFAULT, x.toFloat(), y.toFloat(), 30f, 30f)
//        drawTexture(image.textureId, x.toFloat(), y.toFloat(), 30F, 30F)
        cursorItem.type.also {
//            drawTexture(image?.textureId, x + 5F, y + 15F, 15F, 15F / size.size)
            val itemTexture = it.texture ?: return@also
            RendererManager.UIRenderer.render(itemTexture, Model.DEFAULT, x.toFloat() + 10F, y.toFloat() + 20F, tileSizeF, tileSizeF * it.state.scale)
        }
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
                inventory.giveItem(cursorItem.clone)
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

        val hoveredItem = inventory.getItemOfMouse(x, y)!!

        cursorText = if (hoveredItem.type !== Material.AIR)
            (hoveredItem.run { "$display $amount" })
        else ""

        if (inventory.opened) {
            when {
                leftClick -> {
                    if (cursorItem.type !== hoveredItem.type) {
                        inventory.setItemFromMouse(x, y, cursorItem)
                        cursorItem = hoveredItem
                    } else {
                        hoveredItem.compareItem(cursorItem)
                    }
                }

                midClick -> {
                    if (cursorItem.type === Material.AIR) {
                        cursorItem = hoveredItem.takeHalf()
                    } else if (cursorItem.type == hoveredItem.type) {
                        cursorItem.compareItem(hoveredItem.takeHalf())
                    }
                }

                rightClick -> {
                    if (cursorItem.type == Material.AIR) {
                        cursorItem = hoveredItem.takeOne()
                    } else if (cursorItem.type == hoveredItem.type) {
                        cursorItem.compareItem(hoveredItem.takeOne())
                    }
                }
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

            val hotBarItem = ClientPlayer.inventory.getItem(inventory.selectedHotBar, 0) ?: return

            when {
                leftPress -> {
                    if (cursorItem.type === Material.AIR && hotBarItem.type === Material.AIR) {
                        destroy()
                    } else if (!this.hitBox.clone.transform(1F, 0F, -1F, 0F).intersects(ClientPlayer.hitBox))
                        if (cursorItem.type !== Material.AIR) {
                            if (type === cursorItem.type) return
                            place(cursorItem.type)
                            cursorItem.decrement()
                        } else {
                            if (type === hotBarItem.type) return
                            place(hotBarItem.type)
                            hotBarItem.decrement()
                        }
                }

                rightPress -> {
                    if (cursorItem.type === Material.AIR) return
                    cursorItem.drop(ClientPlayer.centerPos)
                }

                midPress -> {
                    val item = Item(type, type.maxStackSize)
                    if (inventory.opened) {
                        cursorItem = item
                        cursorItemSlot = inventory.firstEmptySlot(!inventory.opened)
                    } else {
                        val targetSlot = inventory.findIdentify(item.type, !inventory.opened).first
                        if (targetSlot == -1) inventory.setItem(inventory.firstEmptySlot(!inventory.opened), item)
                        else inventory.selectedHotBar = targetSlot
                    }
                }
            }
        }
    }
}