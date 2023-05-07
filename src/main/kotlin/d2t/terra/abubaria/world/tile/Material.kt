package d2t.terra.abubaria.world.tile

import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.particleSize
import d2t.terra.abubaria.inventory.inSlotPos
import d2t.terra.abubaria.inventory.inSlotSize
import d2t.terra.abubaria.lwjgl.Image
import d2t.terra.abubaria.lwjgl.loadImage
import d2t.terra.abubaria.world.tile.MaterialState.*

enum class MaterialState(val offset: Double) {
    BOTTOM(0.5), UPPER(.0), FULL(.0)
}

enum class Material(
    val id: Int,
    path: String?,
    val display: String,
    val maxStackSize: Int = 9999,
    val state: MaterialState = FULL,
    val collideable: Boolean = true,
    val height: Int = tileSize,
    val friction: Double = .03
) {
    AIR(0, null, "", 0, FULL, false, tileSize, .005),
    STONE(1, "stone", "Stone"),
    GRASS(2, "grass", "Grass"),
    DIRT(3, "dirt", "Dirt"),
    STONE_HALF_DOWN(4, "stone_half_down", "Stone Slab", 9999, BOTTOM, true, tileSize / 2),
    STONE_HALF_UP(4, "stone_half_up", "Stone Slab", 9999, UPPER, true, tileSize / 2);

    val texture: Image? = if (path === null) null else loadImage("block/$path.png")
    val invSizes = scaleToSlotSize()
    val slices = Array(particleSize) { Array(particleSize) { Image() } }.apply {
        if (texture === null) return@apply
        for (y in 0 until particleSize) {
            for (x in 0 until particleSize) {
                val tileWidth = texture.width / particleSize
                val tileHeight = texture.height / particleSize
                this[x][y] = texture.subImage(x * tileWidth, y * tileHeight, tileWidth, tileHeight)
                this[x][y].apply a@{
                    if (width == 0) width = tileWidth
                    if (height == 0) height = tileHeight
                }
            }
        }
    }

    private fun scaleToSlotSize(): Pair<Int, Int> {
        var width = tileSize.toDouble()
        var height = height.toDouble()

        while (width < inSlotSize && height < inSlotSize) {
            width += 1
            height += 1.0 * (1.0 - state.offset)
        }

        return width.toInt() to height.toInt()
    }


}