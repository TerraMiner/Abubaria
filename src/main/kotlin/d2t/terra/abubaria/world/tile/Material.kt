package d2t.terra.abubaria.world.tile

import d2t.terra.abubaria.GamePanel.tileSize

import d2t.terra.abubaria.world.tile.MaterialState.BOTTOM
import d2t.terra.abubaria.world.tile.MaterialState.FULL
import padTexture
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
    val state: MaterialState = FULL,
    val collideable: Boolean = true,
    val height: Int = tileSize,
    val friction: Double = .03
) {
    AIR(0, null,"", FULL, false, tileSize, .005),
    STONE(1, "stone","Stone"),
    GRASS(2, "grass","Grass"),
    DIRT(3, "dirt","Dirt"),
    STONE_HALF_DOWN(4, "stone_half_down", "Stone Slab", BOTTOM, true, tileSize / 2);

    val texture: BufferedImage? = if (path == null) null else scaleImage(
        readImage("block/$path.png"), tileSize, height)
}