package d2t.terra.abubaria.world.tile

import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.particleSize

import d2t.terra.abubaria.world.tile.MaterialState.BOTTOM
import d2t.terra.abubaria.world.tile.MaterialState.FULL
import lwjgl.Image
import lwjgl.loadImage
import readImage
import scaleImage

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

enum class MaterialState(val offset: Int) {
    BOTTOM(tileSize / 2), UPPER(0), FULL(0)
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
    AIR(0, null,"",0, FULL, false, tileSize, .005, ),
    STONE(1, "stone","Stone"),
    GRASS(2, "grass","Grass"),
    DIRT(3, "dirt","Dirt"),
    STONE_HALF_DOWN(4, "stone_half_down", "Stone Slab",9999, BOTTOM, true, tileSize / 2);

//    val texture: BufferedImage? = if (path == null) null else scaleImage(
//        readImage("block/$path.png"), tileSize, height)
    val texture: Image? = if (path === null) null else loadImage("block/$path.png")
    val slices = Array(particleSize) { Array(particleSize) { Image() } }.apply {
        if (texture === null) return@apply
        for (y in 0 until particleSize) {
            for (x in 0 until particleSize) {
                val tileWidth = texture.width / particleSize
                val tileHeight = texture.height / particleSize
                this[x][y] = texture.subImage(x*tileWidth, y*tileHeight, tileWidth, tileHeight)
                this[x][y].apply a@{
                    if (width == 0) width = tileWidth
                    if (height == 0) height = tileHeight
                }
            }
        }
    }
}