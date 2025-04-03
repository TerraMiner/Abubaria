package d2t.terra.abubaria

import d2t.terra.abubaria.entity.impl.ClientPlayer
import d2t.terra.abubaria.geometry.box.BlockCollisionBox
import d2t.terra.abubaria.inventory.Item
import d2t.terra.abubaria.io.devices.MouseHandler
import d2t.terra.abubaria.io.fonts.TextHorAligment
import d2t.terra.abubaria.io.fonts.TextHorPosition
import d2t.terra.abubaria.io.graphics.Color
import d2t.terra.abubaria.world.block.Block
import d2t.terra.abubaria.world.material.Material
import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.Texture
import d2t.terra.abubaria.io.graphics.render.RenderDimension
import d2t.terra.abubaria.io.graphics.render.Renderer
import d2t.terra.abubaria.io.graphics.render.UI_CURSOR_LAYER
import d2t.terra.abubaria.io.graphics.render.UI_CURSOR_TEXT_LAYER
import d2t.terra.abubaria.io.graphics.render.WORLD_DEBUG_LAYER
import d2t.terra.abubaria.util.print
import d2t.terra.abubaria.world.Camera
import d2t.terra.abubaria.world.material.MaterialState
import java.util.StringJoiner

object Cursor {
    var cursorText = ""
    var mouseOnHud = false

    var currentBlock: Block? = null

    private val texture by lazy { Texture.get("cursor/cursor.png") }

    val world = GamePanel.world

    val inventory get() = ClientPlayer.inventory

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

        MouseHandler.onMousePress(0, ::lmbPressed)
        MouseHandler.onMousePress(1, ::rmbPressed)
        MouseHandler.onMousePress(2, ::mmbPressed)
    }

    private fun getBlock(x: Float, y: Float): Block? {
        return world.getBlockAt(Camera.getWorldBlockX(x), Camera.getWorldBlockY(y))
    }

    fun draw() {
        if (Client.debugMode && !mouseOnHud) {
            val type = currentBlock?.type ?: Material.STONE
            val blockX = Camera.getWorldBlockX(MouseHandler.x)
            val blockY = Camera.getWorldBlockY(MouseHandler.y)
            val x = blockX shl blockShiftBits
            val y = blockY shl blockShiftBits
            val offset = type.state.offset
            val soy = y + offset * tileSizeF
            val w = tileSizeF
            val h = tileSizeF * type.scale
            Renderer.renderText(
                "$blockX $blockY",
                x.toFloat(),
                y.toFloat(),
                18,
                color = Color.GREEN,
                textHorAligment = GamePanel.alignX,
                textHorPosition = GamePanel.positionX,
                textVerAlignment = GamePanel.alignY,
                textVerPosition = GamePanel.positionY,
                dim = RenderDimension.WORLD,
                zIndex = WORLD_DEBUG_LAYER + .01f,
                ignoreCamera = false
            )
            Renderer.renderFilledRectangle(
                x.toFloat(), soy, w, h,
                color = Color(0x55FF557F.toInt()),
                dim = RenderDimension.WORLD,
                zIndex = WORLD_DEBUG_LAYER + -.001f,
                ignoreCamera = false
            )
        }
        val x = MouseHandler.x.toInt()
        val y = MouseHandler.y.toInt()

        Renderer.render(
            texture,
            Model.DEFAULT,
            x.toFloat(),
            y.toFloat(),
            30f,
            30f,
            zIndex = UI_CURSOR_LAYER,
            dim = RenderDimension.SCREEN
        )
        val size = slotSize - inSlotPos * 2f

        inventory.cursorItem.also {
            val itemTexture = it.type.texture ?: return@also
            Renderer.render(
                itemTexture,
                Model.DEFAULT,
                x + 10f,
                y + (size * it.type.state.offset) + 10f,
                size,
                size - (size * it.type.state.scale.toFloat()),
                zIndex = UI_CURSOR_LAYER,
                dim = RenderDimension.SCREEN
            )
        }

        Renderer.renderText(
            cursorText, x + size, y + size, 14, zIndex = UI_CURSOR_TEXT_LAYER,
            textHorAligment = TextHorAligment.CENTER,
            textHorPosition = TextHorPosition.CENTER,
            dim = RenderDimension.SCREEN
        )

    }

    fun update() {
        val x = MouseHandler.x
        val y = MouseHandler.y

        inventory.updateMouseSlot(x.toInt(), y.toInt())
        currentBlock = getBlock(x, y)

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

    private fun lmbPressed(x: Int, y: Int) {
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
    }

    private fun rmbPressed(x: Int, y: Int) {
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
    }

    private fun mmbPressed(x: Int, y: Int) {
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