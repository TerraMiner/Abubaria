package d2t.terra.abubaria.entity.player.inventory

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.hud.Hud
import d2t.terra.abubaria.location.HitBox
import d2t.terra.abubaria.world.tile.Material
import lwjgl.drawTexture

const val slotSize = 42
const val diff = 20

data class Inventory(val xSize: Int, val ySize: Int) {
    val items = Array(xSize) { Array(ySize) { Item(Material.AIR, 0) } }

    var selectedHotBar = 0

    init {
        items[5][0] = Item(Material.STONE_HALF_DOWN,1)
    }

    val hotBar get() = items[0]

    var opened = false

    var hoveredSlot = -1 to -1

    fun scrollHotBar(i: Int) {
        selectedHotBar += i
        if (selectedHotBar < 0) selectedHotBar = ClientPlayer.inventory.xSize - 1
        if (selectedHotBar >= ClientPlayer.inventory.xSize) selectedHotBar = 0
    }

    fun getItemMouse(mouseX: Int, mouseY: Int) =
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


    private val openBound = HitBox(0 + diff, 0 + diff, xSize * slotSize, ySize * slotSize)
    private val closeBound = HitBox(0 + diff, 0 + diff, xSize * slotSize, slotSize)

    val inventoryBound get() = if (opened) openBound else closeBound

    fun firstEmptySlot(): Pair<Int, Int> {
        items.forEachIndexed { x, items ->
            items.forEachIndexed yFor@{ y, item ->
                if (!opened && y != 0) return@yFor
                if (item.type === Material.AIR) return x to y
            }
        }
        return -1 to -1
    }

    fun findIdentify(type: Material): Pair<Int, Int> {
        items.forEachIndexed { x, items ->
            items.forEachIndexed yFor@{ y, item ->
                if (!opened && y != 0) return@yFor
                if (item.type === type) return x to y
            }
        }
        return -1 to -1
    }

    fun draw() {
        if (Hud.inventory.opened) {
            for (x in 0 until Hud.inventory.xSize) {
                for (y in 0 until Hud.inventory.ySize) {

                    val invX = (x * slotSize) + diff
                    val invY = (y * slotSize) + diff

                    if (selectedHotBar == x && y == 0)
                        drawTexture(Hud.selectedSlot.textureId, invX, invY, slotSize, slotSize)
                    else drawTexture(Hud.slot.textureId, invX, invY, slotSize, slotSize)

                    val item = Hud.inventory.getItem(x, y) ?: continue
                    drawTexture(
                        item.type.texture?.textureId,
                        invX + 8,
                        invY + 8 + item.type.state.offset,
                        slotSize - 16,
                        slotSize - 16 - item.type.height
                    )
                }
            }
        } else {

            for (x in 0 until Hud.inventory.xSize) {

                val invX = (x * slotSize) + diff
                val invY = diff

                if (selectedHotBar == x)
                    drawTexture(Hud.selectedSlot.textureId, invX, invY, slotSize, slotSize)
                else drawTexture(Hud.slot.textureId, invX, invY, slotSize, slotSize)

                val item = Hud.inventory.getItem(x, 0) ?: continue
                drawTexture(
                    item.type.texture?.textureId,
                    invX + 8,
                    invY + 8 + item.type.state.offset,
                    slotSize - 16,
                    slotSize - 16 - item.type.height
                )

            }
        }
    }
}