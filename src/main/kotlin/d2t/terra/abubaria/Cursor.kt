package d2t.terra.abubaria

import d2t.terra.abubaria.entity.impl.ClientPlayer
import d2t.terra.abubaria.geometry.box.BlockCollisionBox
import d2t.terra.abubaria.inventory.Item
import d2t.terra.abubaria.io.devices.MouseHandler
import d2t.terra.abubaria.io.fonts.TextHorAligment
import d2t.terra.abubaria.io.fonts.TextHorPosition
import d2t.terra.abubaria.io.graphics.Color
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.io.graphics.render.RendererManager
import d2t.terra.abubaria.world.block.Block
import d2t.terra.abubaria.world.material.Material
import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.Texture
import d2t.terra.abubaria.io.graphics.render.BatchSession
import d2t.terra.abubaria.util.Cooldown
import d2t.terra.abubaria.util.print
import java.util.StringJoiner
import kotlin.math.floor

object Cursor {
    var cursorText = ""
    var mouseOnHud = false

    var currentBlock: Block? = null

    private val texture by lazy { Texture("cursor/cursor.png") }

    val world = GamePanel.world

    val inventory get() = ClientPlayer.inventory

    var lmbDrag = false
    var rmbDrag = false
    var mmbDrag = false

    init {
        MouseHandler.onMouseClick(0) { _, _ ->
            if (mouseOnHud) {
                if (inventory.opened) {
                    val hoveredItem = inventory.hoveredItem ?: return@onMouseClick
                    if (inventory.cursorItem.type !== hoveredItem.type) {
                        inventory.hoveredItem = inventory.cursorItem
                        inventory.cursorItem = hoveredItem
                        inventory.cursorItemSlot = inventory.hoveredSlot
                    } else {
                        hoveredItem.compareItem(inventory.cursorItem)
                    }
                } else {
                    inventory.selectedHotBar = inventory.hoveredSlot
                }
            } else {
                val currentBlock = currentBlock ?: return@onMouseClick
                val item = inventory.cursorItem.takeIf { it.type !== Material.AIR }
                    ?: inventory.hotBarItem?.takeIf { it.type !== Material.AIR }
                if (item === null) {
                    currentBlock.destroy()
                } else if (currentBlock.type !== item.type && !BlockCollisionBox(
                        currentBlock,
                        item.type
                    ).intersects(ClientPlayer.collisionBox)
                ) {
                    currentBlock.place(item.type)
                    item.decrement()
                }
            }
        }

        MouseHandler.onMouseClick(1) { _, _ ->
            if (mouseOnHud) {
                if (inventory.opened) {
                    val hoveredItem = inventory.hoveredItem ?: return@onMouseClick
                    if (inventory.cursorItem.type === Material.AIR && hoveredItem.type === Material.AIR) return@onMouseClick
                    if (inventory.cursorItem.type == Material.AIR) {
                        inventory.cursorItem = hoveredItem.takePart()
                        inventory.cursorItemSlot = inventory.hoveredSlot
                    } else if (inventory.cursorItem.type == hoveredItem.type) {
                        val leftOver = hoveredItem.compareItem(inventory.cursorItem.takeOne())
                        inventory.cursorItem.amount += leftOver
                    } else {
                        inventory.hoveredItem = inventory.cursorItem.takeOne()
                    }
                }
            } else {
                if (inventory.cursorItem.type === Material.AIR) return@onMouseClick
                inventory.cursorItem.drop(ClientPlayer.centerPos)
            }
        }

        MouseHandler.onMouseClick(2) { _, _ ->
            if (mouseOnHud) {
                if (inventory.opened) {
                    val hoveredItem = inventory.hoveredItem ?: return@onMouseClick
                    if (inventory.cursorItem.type === Material.AIR && hoveredItem.type === Material.AIR) return@onMouseClick
                    if (inventory.cursorItem.type === Material.AIR) {
                        inventory.cursorItem = hoveredItem.cloneMaxSized()
                        inventory.cursorItemSlot = inventory.hoveredSlot
                    } else if (inventory.cursorItem.type == hoveredItem.type) {
                        hoveredItem.compareItem(inventory.cursorItem.cloneMaxSized())
                    } else {
                        inventory.hoveredItem = inventory.cursorItem.cloneMaxSized()
                    }
                }
            } else {
                val currentBlock = currentBlock ?: return@onMouseClick
                val item = Item(currentBlock.type, currentBlock.type.maxStackSize)
                if (item.type === Material.AIR) return@onMouseClick
                if (inventory.opened) {
                    inventory.cursorItem = item
                    inventory.cursorItemSlot = inventory.firstIdentSlot(false, item.type, Material.AIR)
                } else {
                    val targetSlot = inventory.firstSameTypeSlot(true, item.type)
                    if (targetSlot == -1) inventory.giveItem(item)
                    else inventory.selectedHotBar = targetSlot
                }
            }
        }

        MouseHandler.onMouseDrag(0) { _, _ ->
            lmbDrag = true
        }

        MouseHandler.onMouseDrag(1) { _, _ ->
            rmbDrag = true
        }

        MouseHandler.onMouseDrag(2) { _, _ ->
            mmbDrag = true
        }
    }

    private fun getGamePositionX(x: Int): Int {
        var worldX = (x.toFloat() / tileSizeF) - (Window.centerX / tileSizeF) + (ClientPlayer.location.x / tileSizeF)

        if (Window.centerX > ClientPlayer.location.x) worldX = x.toFloat() / tileSizeF

        if (Window.width - Window.centerX > world.width - ClientPlayer.location.x) worldX =
            world.width.toFloat() / tileSizeF - (Window.width.toFloat() / tileSizeF - x.toFloat() / tileSizeF)

        return floor(worldX).toInt()
    }

    private fun getGamePositionY(y: Int): Int {
        var worldY =
            y.toFloat() / tileSizeF - Window.centerY.toFloat() / tileSizeF + ClientPlayer.location.y / tileSizeF

        if (Window.centerY > ClientPlayer.location.y) worldY = y.toFloat() / tileSizeF

        if (Window.height - Window.centerY > world.height - ClientPlayer.location.y) worldY =
            world.height.toFloat() / tileSizeF - (Window.height.toFloat() / tileSizeF - y.toFloat() / tileSizeF)

        return floor(worldY).toInt()
    }

    private fun getBlockPosition(x: Int, y: Int): Block? {
        return world.getBlockAt(getGamePositionX(x), getGamePositionY(y))
    }

    fun draw(session: BatchSession) {
//        if (Client.debugMode && !mouseOnHud) {
//            currentBlock?.apply {
//                RendererManager.WorldRenderer.apply {
//                    shader.performSnapshot(shader.colorPalette) {
//                        renderText(
//                            "$x $y",
//                            x * tileSizeF,
//                            y * tileSizeF,
//                            .3f,
//                            color = Color.GREEN,
//                            textHorAligment = GamePanel.alignX,
//                            textHorPosition = GamePanel.positionX,
//                            textVerAlignment = GamePanel.alignY,
//                            textVerPosition = GamePanel.positionY,
//                        )
//                    }
//                }
//            }
//        }
            val x = MouseHandler.x.toInt()
            val y = MouseHandler.y.toInt()

        session.render(texture, Model.DEFAULT, x.toFloat(), y.toFloat(), 30f, 30f)
            val size = slotSize - inSlotPos * 2f

            inventory.cursorItem.also {
                val itemTexture = it.type.texture ?: return@also
                session.render(
                    itemTexture,
                    Model.DEFAULT,
                    x + 10f,
                    y + (size * it.type.state.offset) + 10f,
                    size,
                    size - (size * it.type.state.scale.toFloat())
                )
            }

//        RendererManager.UIRenderer.renderText(
//            cursorText, x + size, y + size, .2f,
//            textHorAligment = TextHorAligment.CENTER,
//            textHorPosition = TextHorPosition.CENTER
//        )
    }

    fun update() {
        val x = MouseHandler.x.toInt()
        val y = MouseHandler.y.toInt()

        lmbDrag()
        rmbDrag()
        mmbDrag()

        inventory.updateMouseSlot(x, y)
        currentBlock = getBlockPosition(x, y)

        val bound = inventory.inventoryBound
        mouseOnHud = (x >= bound.x && y >= bound.y && x < bound.x + bound.sizeX && y < bound.y + bound.sizeY)

        cursorText =
            inventory.cursorItem.takeIf { it.type !== Material.AIR }?.let {
                val joiner = StringJoiner("\n")
                joiner.add(it.display)
                if (it.type.maxStackSize != 1 || it.amount != 1) joiner.add("(${it.amount})")
                joiner.toString()
            }
                ?: inventory.hoveredItem?.takeIf { it.type !== Material.AIR }?.display
                        ?: currentBlock?.takeIf { it.type !== Material.AIR }?.type?.display ?: ""

        tryStoreCursorItemToInventory()
    }

    private fun lmbDrag() {
        if (lmbDrag) {
            if (mouseOnHud) {
                if (!inventory.opened) {
                    inventory.selectedHotBar = inventory.hoveredSlot
                }
            } else {
                val currentBlock = currentBlock ?: return
                val item = inventory.cursorItem.takeIf { it.type !== Material.AIR }
                    ?: inventory.hotBarItem?.takeIf { it.type !== Material.AIR }
                if (item === null) {
                    currentBlock.destroy()
                } else if (currentBlock.type !== item.type && !BlockCollisionBox(currentBlock, item.type).intersects(
                        ClientPlayer.collisionBox
                    )
                ) {
                    currentBlock.place(item.type)
                    item.decrement()
                    if (item.amount <= 0) MouseHandler.forceReleaseButton(0)
                }
            }
            lmbDrag = false
        }

    }

    private fun rmbDrag() {
        if (rmbDrag) {
            if (mouseOnHud) {
                if (inventory.opened) {
                    val hoveredItem = inventory.hoveredItem ?: return
                    if (inventory.cursorItem.type === Material.AIR && hoveredItem.type === Material.AIR) return
                    if (inventory.cursorItem.type == hoveredItem.type) {
                        val leftOver = hoveredItem.compareItem(inventory.cursorItem.takeOne())
                        inventory.cursorItem.amount += leftOver
                    } else if (hoveredItem.type === Material.AIR) {
                        inventory.hoveredItem = inventory.cursorItem.takeOne()
                    }
                }
            }
            rmbDrag = false
        }
    }

    private fun mmbDrag() {
        if (mmbDrag) {
            if (mouseOnHud) {
                if (inventory.opened) {
                    val hoveredItem = inventory.hoveredItem ?: return
                    if (inventory.cursorItem.type === Material.AIR && hoveredItem.type === Material.AIR) return
                    if (inventory.cursorItem.type === Material.AIR) {
                        inventory.cursorItem = hoveredItem.cloneMaxSized()
                        inventory.cursorItemSlot = inventory.hoveredSlot
                    } else if (inventory.cursorItem.type == hoveredItem.type) {
                        hoveredItem.compareItem(inventory.cursorItem.cloneMaxSized())
                    } else {
                        inventory.hoveredItem = inventory.cursorItem.cloneMaxSized()
                    }
                }
            }
            mmbDrag = false
        }
    }

    private fun tryStoreCursorItemToInventory() {
        if (!inventory.opened
            && inventory.cursorItem.type !== Material.AIR
            && inventory.cursorItemSlot != -1
        ) {
            if (inventory.items[inventory.cursorItemSlot].type === Material.AIR) {
                inventory.items[inventory.cursorItemSlot] = inventory.cursorItem.clone
            } else {
                inventory.giveItem(inventory.cursorItem.clone)
            }
            inventory.cursorItem.remove()
            inventory.cursorItemSlot = -1
        }
    }

}