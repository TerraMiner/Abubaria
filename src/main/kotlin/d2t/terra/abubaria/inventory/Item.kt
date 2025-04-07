package d2t.terra.abubaria.inventory

import d2t.terra.abubaria.entity.impl.item.ItemEntity
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.material.Material
import d2t.terra.abubaria.io.graphics.texture.Model
import d2t.terra.abubaria.io.graphics.render.Layer
import d2t.terra.abubaria.io.graphics.render.RenderDimension
import d2t.terra.abubaria.io.graphics.render.Renderer
import kotlin.math.ceil
import kotlin.math.floor

class Item(type: Material = Material.AIR, amount: Int = 1) {

    var amount = amount
        set(value) {
            field = value
            if (value <= 0 && type != Material.AIR) {
                type = Material.AIR
            } else if (value > type.maxStackSize) {
                field = type.maxStackSize
            }
        }

    var type = type
        set(value) {
            field = value
            if (value === Material.AIR && amount != 0) {
                amount = 0
            }
        }

    var lore = mutableListOf<String>()
    var display = type.display

    val clone
        get() = Item(type, amount).also {
            it.lore = lore
            it.display = display
        }

    fun takePart(part: Double = 2.0): Item {
        val count: Double
        val material = type
        if (amount > 1) {
            count = amount / part
            amount = floor(count).toInt()
        } else {
            count = 1.0
            amount = 0
        }
        return Item(material, ceil(count).toInt())
    }

    fun takeOne(): Item {
        val material = type
        amount--
        return Item(material, 1)
    }

    fun cloneMaxSized() = clone.also { it.amount = it.type.maxStackSize }

    fun compareItem(item: Item): Int {
        val sumAmount = item.amount + amount
        return if (sumAmount > type.maxStackSize) {
            amount = type.maxStackSize
            val leftOver = sumAmount - type.maxStackSize
            item.amount = leftOver
            leftOver
        } else {
            amount = sumAmount
            item.amount = 0
            0
        }
    }

    fun decrement() {
        if (type == Material.AIR) return
        amount--
    }

    fun increment() {
        if (type == Material.AIR) return
        amount++
    }

    fun remove() {
        type = Material.AIR
        amount = 0
    }

    fun drop(location: Location, pickupDelay: Int = 2500, dx: Float = .7f, dy: Float = -.5f): ItemEntity {
        return ItemEntity(clone, location, pickupDelay).apply {
            movement(dx * location.direction.offset, dy)
        }.also {
            it.spawn()
            remove()
        }
    }

    fun draw(
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        val texture = type.texture ?: return
        Renderer.render(texture, Model.DEFAULT, x, y, width, height, layer = Layer.UI_HUD_CONTENTS_LAYER, dim = RenderDimension.SCREEN)
    }

}