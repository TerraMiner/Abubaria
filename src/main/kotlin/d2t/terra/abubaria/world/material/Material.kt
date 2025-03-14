package d2t.terra.abubaria.world.material

import d2t.terra.abubaria.GamePanel.tileSizeF
import d2t.terra.abubaria.util.getCoords
import d2t.terra.abubaria.world.material.MaterialState.*
import d2t.terra.abubaria.world.particleSize
import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.Texture

enum class Material(
    path: String?,
    val display: String,
    val maxStackSize: Int = 9999,
    val state: MaterialState = FULL,
    val collideable: Boolean = true,
    val scale: Float = 1f,
    val friction: Float = .03f
) {
    AIR(null, "", 0, FULL, false, tileSizeF, .005f),
    STONE("stone", "Stone"),
    GRASS("grass", "Grass"),
    DIRT("dirt", "Dirt"),
    STONE_HALF_DOWN("stone_half_down", "Stone Slab Bottom", 9999, BOTTOM, true, .5f),
    STONE_HALF_UP("stone_half_up", "Stone Slab Upper", 9999, UPPER, true, .5f);

//    val image: Image? = if (path === null) null else loadImage("block/$path.png")
    val texture: Texture? = if (path === null) null else Texture("block/$path.png")

    val particles = (particleSize * particleSize).let { particlesCapacity ->
        val texture = texture ?: return@let emptyArray<Model>()
        Array(particlesCapacity) {
            val pos = getCoords(it, texture.width, texture.height)
            Model(let {
                val normalizedTexX = pos.x.toFloat() / texture.width
                val normalizedTexY = pos.y.toFloat() / texture.height
                val normalizedTexWidth = particleSize.toFloat() / texture.width
                val normalizedTexHeight = particleSize.toFloat() / texture.height
                floatArrayOf(
                    /*0f, 1f,*/ normalizedTexX, normalizedTexY + normalizedTexHeight,
                    /*1f, 1f,*/ normalizedTexX + normalizedTexWidth, normalizedTexY + normalizedTexHeight,
                    /*1f, 0f,*/ normalizedTexX + normalizedTexWidth, normalizedTexY,
                    /*0f, 0f,*/ normalizedTexX, normalizedTexY
                )
            })
        }
    }

//    val slices = Array(particleSize) { Array(particleSize) { Model() } }.apply {
//        if (image === null) return@apply
//        for (y in 0 until particleSize) {
//            for (x in 0 until particleSize) {
//                val tileWidth = image.width / particleSize
//                val tileHeight = image.height / particleSize
//                this[x][y] = image.subImage(x * tileWidth, y * tileHeight, tileWidth, tileHeight)
//                this[x][y].apply a@{
//                    if (width == 0) width = tileWidth
//                    if (height == 0) height = tileHeight
//                }
//            }
//        }
//    }


}