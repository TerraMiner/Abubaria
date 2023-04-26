package d2t.terra.abubaria.entity.player.inventory

import d2t.terra.abubaria.world.tile.Material

val maxStackSize = 9999
data class Item(var type: Material = Material.AIR, var amount: Int = 1) {
    var lore = mutableListOf<String>()
    var display = type.display


}