package d2t.terra.abubaria.inventory

import d2t.terra.abubaria.Cursor.inventory
import d2t.terra.abubaria.entity.impl.ClientPlayer
import d2t.terra.abubaria.geometry.box.CollisionBox
import d2t.terra.abubaria.hud.Hud
import d2t.terra.abubaria.io.graphics.render.RendererManager
import d2t.terra.abubaria.diff
import d2t.terra.abubaria.inSlotPos
import d2t.terra.abubaria.io.devices.MouseHandler
import d2t.terra.abubaria.io.fonts.TextHorAligment
import d2t.terra.abubaria.io.fonts.TextHorPosition
import d2t.terra.abubaria.world.material.Material
import d2t.terra.abubaria.slotSize
import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.util.getCoords
import d2t.terra.abubaria.util.getIndex


data class Inventory(val xSize: Int, val ySize: Int) {
    val items = Array(xSize * ySize) { Item(Material.AIR, 0) }
    val hotBarItem get() = getItem(selectedHotBar, 0)

    var selectedHotBar = 0

    init {
        items[getIndex(5, 0, xSize, ySize)] = Item(Material.STONE_HALF_DOWN, Material.STONE_HALF_DOWN.maxStackSize)
        items[getIndex(6, 0, xSize, ySize)] = Item(Material.STONE_HALF_UP, Material.STONE_HALF_UP.maxStackSize)

        MouseHandler.onMouseScroll("Y") { offset ->
            ClientPlayer.inventory.scrollHotBar(-offset.toInt())
        }
    }

    var opened = false

    var cursorItem: Item = Item()
    var cursorItemSlot: Int = -1

    var hoveredSlot = -1
    var hoveredItem
        get() = items.getOrNull(hoveredSlot)
        set(value) {
            if (hoveredSlot != -1) {
                items[hoveredSlot] = value ?: Item()
            }
        }

    private val openBound = CollisionBox(0f + diff, 0f + diff, xSize * slotSize.toFloat(), ySize * slotSize.toFloat())
    private val closeBound = CollisionBox(0f + diff, 0f + diff, xSize * slotSize.toFloat(), slotSize.toFloat())

    val inventoryBound get() = if (opened) openBound else closeBound

    fun scrollHotBar(i: Int) {
        selectedHotBar += i
        if (selectedHotBar < 0) selectedHotBar = ClientPlayer.inventory.xSize - 1
        if (selectedHotBar >= ClientPlayer.inventory.xSize) selectedHotBar = 0
    }

    fun updateMouseSlot(mouseX: Int, mouseY: Int) {
        val x = (mouseX - diff.toInt()) / slotSize.toInt()
        val y = (mouseY - diff.toInt()) / slotSize.toInt()

        if (!inBound(x, y)) {
            hoveredSlot = -1
            return
        }

        hoveredSlot = getIndex(x, y)
    }

    fun getItem(x: Int, y: Int) = items.getOrNull(getIndex(x, y))
    fun setItem(x: Int, y: Int, item: Item) {
        if (!inBound(x, y)) return
        items[getIndex(x, y)] = item
    }

    fun setItem(pair: Pair<Int, Int>, item: Item) {
        setItem(pair.first, pair.second, item)
    }

    fun setItemFromMouse(mouseX: Int, mouseY: Int, item: Item) {
        val x = (mouseX - diff) / slotSize
        val y = (mouseY - diff) / slotSize
        setItem(x.toInt(), y.toInt(), item)
    }

    fun giveItem(item: Item) {
        val slot = firstIdentSlot(false, item.type, Material.AIR)
        if (slot == -1) {
            item.drop(ClientPlayer.centerPos)
            return
        }

        items[slot].takeIf { it.type !== Material.AIR }?.let {
            it.compareItem(item)
            if (item.amount != 0) giveItem(item)
        } ?: items.set(slot, item)
    }

    fun firstIdentSlot(onlyHotBar: Boolean, vararg types: Material): Int {
        types.forEach { type ->
            items.forEachIndexed { index, item ->
                val pos = getCoords(index)
                if (onlyHotBar && pos.y != 0) return@forEachIndexed
                if (type === Material.AIR && item.type === Material.AIR || item.type === type && item.amount < type.maxStackSize) return index
            }
        }
        return -1
    }

    fun firstSameTypeSlot(onlyHotBar: Boolean, type: Material): Int {
        items.forEachIndexed { index, item ->
            val pos = getCoords(index)
            if (onlyHotBar && pos.y != 0) return@forEachIndexed
            if (item.type === type && item.amount <= type.maxStackSize) return index
        }
        return -1
    }

    fun draw() {
        val inventory = Hud.inventory
        items.forEachIndexed { index, item ->
            val pos = getCoords(index)
            if (!opened && pos.y != 0) return
            val screenX = (pos.x * slotSize) + diff
            val screenY = (pos.y * slotSize) + diff

            val texture = if (selectedHotBar == pos.x && pos.y == 0) Hud.selectedSlot else Hud.slot

            RendererManager.UIRenderer.render(texture, Model.DEFAULT, screenX, screenY, slotSize, slotSize)

            if (item.type !== Material.AIR) {
                val size = slotSize - inSlotPos * 2f
                item.draw(
                    screenX + inSlotPos,
                    screenY + inSlotPos + (size * item.type.state.offset).toInt(),
                    size,
                    size - (size * item.type.state.scale.toFloat())
                )
                RendererManager.UIRenderer.renderText(
                    "${item.amount}", screenX + inSlotPos + size + inSlotPos / 2, screenY + size, .2f,
                    textHorAligment = TextHorAligment.RIGHT,
                    textHorPosition = TextHorPosition.RIGHT
                )
            }

            if (pos.y == 0) {
                RendererManager.UIRenderer.renderText(
                    inventory.getItem(selectedHotBar, 0)?.display ?: "",
                    24F,
                    -3f,
                    .3f
                )
            }
        }
    }

    fun getCoords(index: Int) = getCoords(index, xSize, ySize)
    fun getIndex(x: Int, y: Int) = getIndex(x, y, xSize, ySize)
    fun inBound(x: Int, y: Int) = x >= 0 && x < xSize && y >= 0 && y <= ySize
}