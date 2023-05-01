package d2t.terra.abubaria.entity.player.inventory

import d2t.terra.abubaria.world.tile.Material

data class Item(private var material: Material = Material.AIR, private var count: Int = 1){
    var lore = mutableListOf<String>()
    var display = material.display

    val type get() = material
    val amount get() = count

    val clone get() = Item(material,count).also {
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

}