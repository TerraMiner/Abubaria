package d2t.terra.abubaria.world.material

import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.GamePanel.tileSizeF
import d2t.terra.abubaria.io.graphics.Image
import d2t.terra.abubaria.io.graphics.loadImage
import d2t.terra.abubaria.world.inSlotSize
import d2t.terra.abubaria.world.material.MaterialState.*
import d2t.terra.abubaria.world.particleSize

enum class Material(
    path: String?,
    val display: String,
    val maxStackSize: Int = 9999,
    val state: MaterialState = FULL,
    val collideable: Boolean = true,
    val height: Float = tileSizeF,
    val size: MaterialSize = MaterialSize.FULL,
    val friction: Float = .03f
) {
    AIR(null, "", 0, FULL, false, tileSizeF, MaterialSize.FULL, .005f),
    STONE("stone", "Stone"),
    GRASS("grass", "Grass"),
    DIRT("dirt", "Dirt"),
    STONE_HALF_DOWN("stone_half_down", "Stone Slab", 9999, BOTTOM, true, tileSizeF / 2, MaterialSize.HALF),
    STONE_HALF_UP("stone_half_up", "Stone Slab", 9999, UPPER, true, tileSizeF / 2, MaterialSize.HALF);

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

    private fun scaleToSlotSize(): Pair<Float, Float> {
        var width = tileSizeF
        var height = height

        while (width < inSlotSize && height < inSlotSize) {
            width += 1F
            height += 1F / size.size
        }

        return width to height
    }


}