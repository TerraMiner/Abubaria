package d2t.terra.abubaria.inventory

import d2t.terra.abubaria.io.graphics.drawString
import d2t.terra.abubaria.io.graphics.drawTexture
import d2t.terra.abubaria.world.material.Material
import kotlin.math.ceil
import kotlin.math.floor

data class Item(private var material: Material = Material.AIR, private var count: Int = 1) {
    var lore = mutableListOf<String>()
    var display = material.display

    val type get() = material
    val amount get() = count

    val clone
        get() = Item(material, count).also {
            it.lore = lore
            it.display = display
        }


    fun setType(type: Material) {
        material = type
        if (material === Material.AIR || count == 0) {
            material = Material.AIR
            count = 0
        }
    }

    fun setAmount(amount: Int) {
        this.count = amount
        checkAmount()
    }

    private fun checkAmount() {
        if (count <= 0) {
            remove()
        } else if (count > material.maxStackSize) {
            count = material.maxStackSize
        }
    }

    fun takeHalf(): Item {
        val count: Double
        val material = material
        if (amount > 1) {
            count = amount / 2.0
            setAmount(floor(count).toInt())
        } else {
            count = 1.0
            setAmount(0)
        }
        return Item(material, ceil(count).toInt())
    }

    fun takeOne(): Item {
        val material = material
        setAmount(count - 1)
        return Item(material, 1)
    }

    fun compareItem(item: Item) {
        val sumAmount = item.count + count
        if (sumAmount > material.maxStackSize) {
            setAmount(material.maxStackSize)
            item.setAmount(sumAmount - material.maxStackSize)
        }
        setAmount(sumAmount)
        item.setAmount(0)
    }

    fun decrement() {
        if (material == Material.AIR) return
        setAmount(count - 1)
    }

    fun increment() {
        if (material == Material.AIR) return
        setAmount(count + 1)
    }

    fun remove() {
        material = Material.AIR
        count = 0
    }

    fun draw(x: Int, y: Int, width: Int, height: Int, withText: Boolean = true, txtMod: Int = 5) {
        drawTexture(type.texture?.textureId, x, y, width, height)
        if (withText) {
            drawString("$count", x, y + height + txtMod, txtMod)
        }
    }

}