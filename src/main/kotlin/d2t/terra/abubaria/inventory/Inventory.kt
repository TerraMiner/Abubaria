package d2t.terra.abubaria.inventory

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.hitbox.HitBox
import d2t.terra.abubaria.hud.Hud
import d2t.terra.abubaria.io.graphics.render.RendererManager
import d2t.terra.abubaria.world.diff
import d2t.terra.abubaria.world.inSlotPos
import d2t.terra.abubaria.world.material.Material
import d2t.terra.abubaria.world.slotSize
import d2t.terra.abubaria.io.graphics.Model


data class Inventory(val xSize: Int, val ySize: Int) {
    val items = Array(xSize) { Array(ySize) { Item(Material.AIR, 0) } }

    var selectedHotBar = 0

    init {
        items[5][0] = Item(Material.STONE_HALF_DOWN, Material.STONE_HALF_DOWN.maxStackSize)
        items[6][0] = Item(Material.STONE_HALF_UP, Material.STONE_HALF_UP.maxStackSize)
    }

    var opened = false

    var hoveredSlot = -1 to -1

    private val openBound = HitBox(0 + diff, 0 + diff, xSize * slotSize, ySize * slotSize)
    private val closeBound = HitBox(0 + diff, 0 + diff, xSize * slotSize, slotSize)

    val inventoryBound get() = if (opened) openBound else closeBound

    fun scrollHotBar(i: Int) {
        selectedHotBar += i
        if (selectedHotBar < 0) selectedHotBar = ClientPlayer.inventory.xSize - 1
        if (selectedHotBar >= ClientPlayer.inventory.xSize) selectedHotBar = 0
    }

    fun getItemOfMouse(mouseX: Int, mouseY: Int) =
        items.getOrNull((mouseX - diff.toInt()) / slotSize.toInt())
            ?.getOrNull((mouseY - diff.toInt()) / slotSize.toInt())

    fun updateMouseSlot(mouseX: Int, mouseY: Int) {
        val x = (mouseX - diff.toInt()) / slotSize.toInt()
        val y = (mouseY - diff.toInt()) / slotSize.toInt()

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
        val x = (mouseX - diff.toInt()) / slotSize.toInt()
        val y = (mouseY - diff.toInt()) / slotSize.toInt()

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
        if (slot.first == -1 || slot.second == -1 || !hasSpace()) {
            item.drop(ClientPlayer.centerPos)
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

    fun firstEmptySlot(onlyHotBar: Boolean): Pair<Int, Int> {
        items.forEachIndexed { x, items ->
            items.forEachIndexed yFor@{ y, item ->
                if (onlyHotBar && y != 0) return@yFor
                if (item.type === Material.AIR) return x to y
            }
        }
        return -1 to -1
    }

    fun hasSpace(): Boolean {
        return items.flatten().any { it.type === Material.AIR }
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
        val width = inventory.xSize
        val height = if (inventory.opened) inventory.ySize else 1
        repeat(width) { x ->
            val invX = (x * slotSize) + diff
            repeat(height) { y ->
                val invY = (y * slotSize) + diff
                val selectedItem = selectedHotBar == x && y == 0
                val texture = if (selectedItem) Hud.selectedSlot else Hud.slot

                RendererManager.UIRenderer.render(texture, Model.DEFAULT, invX, invY, slotSize, slotSize)

                val item = inventory.getItem(x, y)
                if (item != null && item.type !== Material.AIR) {
                    val size = slotSize - inSlotPos * 2f
                    item.draw(
                        invX + inSlotPos,
                        invY + inSlotPos + (size * item.type.state.offset).toInt(),
                        size,
                        size - (size * item.type.state.scale)
                    )
//                    drawString("${item.amount}", x, y + height, textScale, renderer) TODO После того как сделаю цвета и алигны, тут надо рендерить в правом нижнем углу слота
                }

                if (y == 0) {
                    RendererManager.UIRenderer.renderText(inventory.getItem(selectedHotBar, 0)?.display ?: "", 24F, -3f, .3f)
                }
            }
        }
//        else repeat(inventory.xSize) { x ->
//            val invX = (x * slotSize) + diff
//            val selectedItem = selectedHotBar == x
//
//            val texture = if (selectedItem) Hud.selectedSlot else Hud.slot
//            RendererManager.UIRenderer.render(texture, Model.DEFAULT, invX, diff, slotSize, slotSize)
//
//            val item = inventory.getItem(x, 0)
//            if (item != null && item.type !== Material.AIR) {
//                item.draw(
//                    invX + inSlotPos,
//                    diff + inSlotPos + (inSlotSize * item.type.state.offset).toInt(),
//                    item.type.invSizes.first,
//                    item.type.invSizes.second, withText = true
//                )
//            }
//            drawString(inventory.getItem(selectedHotBar, 0)?.display ?: "", 24F, 12F, .5f)
//        }
    }

}