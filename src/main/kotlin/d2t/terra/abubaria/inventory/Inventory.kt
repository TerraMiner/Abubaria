package d2t.terra.abubaria.inventory

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.hud.Hud
import d2t.terra.abubaria.location.HitBox
import d2t.terra.abubaria.lwjgl.drawString
import d2t.terra.abubaria.lwjgl.drawTexture
import d2t.terra.abubaria.world.tile.Material

const val slotSize = 42
const val inSlotPos = 8
const val inSlotSize = slotSize - 16
const val diff = 20

data class Inventory(val xSize: Int, val ySize: Int) {
    val items = Array(xSize) { Array(ySize) { Item(Material.AIR, 0) } }

    var selectedHotBar = 0

    init {
        items[5][0] = Item(Material.STONE_HALF_DOWN, 1)
    }

    var opened = false

    var hoveredSlot = -1 to -1

    fun scrollHotBar(i: Int) {
        selectedHotBar += i
        if (selectedHotBar < 0) selectedHotBar = ClientPlayer.inventory.xSize - 1
        if (selectedHotBar >= ClientPlayer.inventory.xSize) selectedHotBar = 0
    }

    fun getItemOfMouse(mouseX: Int, mouseY: Int) =
        items.getOrNull((mouseX - diff) / slotSize)?.getOrNull((mouseY - diff) / slotSize)

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

    fun setItemFromMouse(mouseX: Int, mouseY: Int, item: Item) {
        val x = (mouseX - diff) / slotSize
        val y = (mouseY - diff) / slotSize

        GamePanel.cursor.cursorItemSlot = if (item.type === Material.AIR) x to y
        else -1 to -1

        items[x][y] = item
    }

    fun getItem(x: Int, y: Int) = items.getOrNull(x)?.getOrNull(y)
    fun setItem(x: Int, y: Int, item: Item) {
        if (x !in items.indices) return
        if (y !in items[x].indices) return
        items[x][y] = item
    }

    fun setItem(pair: Pair<Int, Int>, item: Item) {
        setItem(pair.first, pair.second, item)
    }

    fun giveItem(item: Item) {
        val slot = firstEmptySlot(false)
        if (slot.first == -1 || slot.second == -1) {
            //drop item
            return
        }

        val identSlot = findIdentify(item.type, false)

        val hasIdent = identSlot.first != -1 && identSlot.second != -1

        if (hasIdent) {
            items[identSlot.first][identSlot.second].compareItem(item)
        }

        if (item.type != Material.AIR) {
            items[slot.first][slot.second] = item
        }
    }


    private val openBound = HitBox(0 + diff, 0 + diff, xSize * slotSize, ySize * slotSize)
    private val closeBound = HitBox(0 + diff, 0 + diff, xSize * slotSize, slotSize)

    val inventoryBound get() = if (opened) openBound else closeBound

    fun firstEmptySlot(onlyHotBar: Boolean): Pair<Int, Int> {
        items.forEachIndexed { x, items ->
            items.forEachIndexed yFor@{ y, item ->
                if (onlyHotBar && y != 0) return@yFor
                if (item.type === Material.AIR) return x to y
            }
        }
        return -1 to -1
    }

    fun findIdentify(type: Material, onlyHotBar: Boolean): Pair<Int, Int> {
        items.forEachIndexed { x, items ->
            items.forEachIndexed yFor@{ y, item ->
                if (onlyHotBar && y != 0) return@yFor
                if (item.type === type) return x to y
            }
        }
        return -1 to -1
    }

    fun draw() {
        val inventory = Hud.inventory
        if (Hud.inventory.opened)
            for (x in 0 until inventory.xSize) {
                val invX = (x * slotSize) + diff
                for (y in 0 until inventory.ySize) {
                    val invY = (y * slotSize) + diff
                    val selectedItem = selectedHotBar == x && y == 0

                    val textureId = if (selectedItem) Hud.selectedSlot.textureId else Hud.slot.textureId
                    drawTexture(textureId, invX, invY, slotSize, slotSize)

                    val item = inventory.getItem(x, y) ?: continue
                    if (item.type == Material.AIR) continue
                    item.draw(
                        invX + inSlotPos,
                        invY + inSlotPos + (inSlotSize * item.type.state.offset).toInt(),
                        item.type.invSizes.first,
                        item.type.invSizes.second, true
                    )
                }
            }
        else for (x in 0 until inventory.xSize) {
            val invX = (x * slotSize) + diff
            val selectedItem = selectedHotBar == x

            val textureId = if (selectedItem) Hud.selectedSlot.textureId else Hud.slot.textureId
            drawTexture(textureId, invX, diff, slotSize, slotSize)

            val item = inventory.getItem(x, 0) ?: continue
            if (item.type == Material.AIR) continue
            item.draw(
                invX + inSlotPos,
                diff + inSlotPos + (inSlotSize * item.type.state.offset).toInt(),
                item.type.invSizes.first,
                item.type.invSizes.second, true
            )
        }
        drawString(inventory.getItem(selectedHotBar, 0)?.display ?: return, 24, 12, 3)
    }
}