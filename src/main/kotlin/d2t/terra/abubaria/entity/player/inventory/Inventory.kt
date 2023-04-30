package d2t.terra.abubaria.entity.player.inventory

import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.hud.Hud
import d2t.terra.abubaria.location.HitBox
import d2t.terra.abubaria.world.tile.Material
import lwjgl.drawTexture
import java.awt.Color
import java.awt.Graphics2D

const val slotSize = 42
const val diff = 20

data class Inventory(val xSize: Int, val ySize: Int) {
    val items = Array(xSize) { Array(ySize) { Item(Material.AIR,0) } }

    val hotBar get() = items[0]

    var opened = false

    var hoveredSlot = -1 to -1

    fun getItemMouse(mouseX: Int, mouseY: Int) = items.getOrNull((mouseX - diff) / slotSize)?.getOrNull((mouseY - diff) / slotSize)

    fun updateMouseSlot(mouseX: Int, mouseY: Int) {
        val x = (mouseX - diff) / slotSize
        val y = (mouseY - diff) / slotSize

        if (0 > x || x >= items.size) {
            hoveredSlot = -1 to -1
            return
        }
        if (0 > y || y >= items[x].size) {
            hoveredSlot = -1 to -1
            return
        }

        hoveredSlot = x to y
    }

    fun setItemMouse(mouseX: Int, mouseY: Int, item: Item) {
        items[(mouseX - diff) / slotSize][(mouseY - diff) / slotSize] = item
    }

    fun getItem(x: Int, y: Int) = items.getOrNull(x)?.getOrNull(y)


    private val openBound = HitBox(0 + diff, 0 + diff, xSize * slotSize, ySize * slotSize)
    private val closeBound = HitBox(0 + diff, 0 + diff, xSize * slotSize, slotSize)

    val inventoryBound get() = if (opened) openBound else closeBound

    fun draw() {
        if (Hud.inventory.opened) {
            for (x in 0 until Hud.inventory.xSize) {
                for (y in 0 until Hud.inventory.ySize) {

                    val invX = (x * slotSize) + diff
                    val invY = (y * slotSize) + diff

                    if (ClientPlayer.selectedHotBar == x && y == 0) {
//                        if (hoveredSlot.first == x && hoveredSlot.second == y)
//                            g2.drawImage(Hud.hoveredSlot,invX, invY, slotSize, slotSize, null)
                        drawTexture(Hud.selectedSlot, invX, invY, slotSize, slotSize)
///*                        else */g2.drawImage(Hud.selectedSlot, invX, invY, slotSize, slotSize, null)
                    } else {
//                        if (hoveredSlot.first == x && hoveredSlot.second == y)
//                            g2.drawImage(Hud.hoveredSlot,invX, invY, slotSize, slotSize, null)
                        drawTexture(Hud.slot, invX, invY, slotSize, slotSize)

//                        /*                        else */g2.drawImage(Hud.slot, invX, invY, slotSize, slotSize, null)
                    }

                    drawTexture(Hud.inventory.getItem(x, y)?.type?.texture, invX + 8, invY + 8,
                        slotSize - 16, slotSize - 16)
//                    g2.drawImage(
//                        Hud.inventory.getItem(x, y)?.type?.texture,
//                        invX + 8, invY + 8,
//                        slotSize - 16, slotSize - 16,
//                        null
//                    )
                }
            }
        } else {

            for (x in 0 until Hud.inventory.xSize) {

                val invX = (x * slotSize) + diff
                val invY = diff

                if (ClientPlayer.selectedHotBar == x) {
//                    if (hoveredSlot.first == x && hoveredSlot.second == 0)
//                        g2.drawImage(Hud.hoveredSlot,invX, invY, slotSize, slotSize, null)
/*                    else */drawTexture(Hud.selectedSlot, invX, invY, slotSize, slotSize)
                } else {
//                    if (hoveredSlot.first == x && hoveredSlot.second == 0)
//                        g2.drawImage(Hud.hoveredSlot,invX, invY, slotSize, slotSize, null)
/*                    else */drawTexture(Hud.slot, invX, invY, slotSize, slotSize)
                }
                drawTexture(Hud.inventory.getItem(x, 0)?.type?.texture, invX + 8, invY + 8,
                    slotSize - 16, slotSize - 16)
//                g2.drawImage(
//                    Hud.inventory.getItem(x, 0)?.type?.texture,
//                    invX + 8, invY + 8,
//                    slotSize - 16, slotSize - 16,
//                    null
//                )

            }
        }
    }
}