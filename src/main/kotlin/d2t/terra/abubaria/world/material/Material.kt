package d2t.terra.abubaria.world.material

import d2t.terra.abubaria.util.getCoords
import d2t.terra.abubaria.world.material.MaterialState.*
import d2t.terra.abubaria.particleSize
import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.Texture

enum class Material(
    path: String?,
    val display: String,
    val state: MaterialState = FULL,
    val isCollideable: Boolean = true,
    val scale: Float = 1f,
    val maxStackSize: Int = 9999
) {
    AIR(null, "", FULL, false, 1f, 0),
    STONE("stone", "Stone"),
    GRASS("grass", "Grass"),
    DIRT("dirt", "Dirt"),
    STONE_HALF_DOWN("stone_half_down", "Stone Slab Bottom", BOTTOM, true, .5f),
    STONE_HALF_UP("stone_half_up", "Stone Slab Upper", UPPER, true, .5f);

    val texture: Texture? = if (path === null) null else Texture.get("block/$path.png")

    val particles = (particleSize * particleSize).let { particlesCapacity ->
        val texture = texture ?: return@let emptyArray<Model>()
        Array(particlesCapacity) {
            val pos = getCoords(it, texture.width, texture.height)
            val uvx = pos.x.toFloat() / texture.width
            val uvy = pos.y.toFloat() / texture.height
            val uvmx = uvx + particleSize.toFloat() / texture.width
            val uvmy = uvy + particleSize.toFloat() / texture.height
            Model(uvx, uvy, uvmx, uvmy)
        }
    }
}