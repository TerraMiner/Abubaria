package d2t.terra.abubaria.world.material

import d2t.terra.abubaria.util.getCoords
import d2t.terra.abubaria.world.material.MaterialState.*
import d2t.terra.abubaria.particleSize
import d2t.terra.abubaria.io.graphics.texture.Model
import d2t.terra.abubaria.io.graphics.texture.Texture
import d2t.terra.abubaria.io.graphics.Color
import d2t.terra.abubaria.io.graphics.Light

enum class Material(
    path: String?,
    val display: String,
    val state: MaterialState = FULL,
    val isCollideable: Boolean = true,
    val scale: Float = 1f,
    val maxStackSize: Int = 9999,
    val lightTransparency: Float = 0.0f, // Прозрачность для света (0.0 - непрозрачный, 1.0 - полностью прозрачный)
    val emitsLight: Boolean = false, // Излучает ли блок свет
    val lightProperties: LightProperties? = null // Свойства света, если блок излучает свет
) {
    AIR(null, "", FULL, false, 1f, 0, 1.0f),
    STONE("stone", "Stone"),
    GRASS("grass", "Grass", lightTransparency = 0.1f),
    DIRT("dirt", "Dirt"),
    STONE_HALF_DOWN("stone_half_down", "Stone Slab Bottom", BOTTOM, true, .5f, 9999, 0.3f),
    STONE_HALF_UP("stone_half_up", "Stone Slab Upper", UPPER, true, .5f, 9999, 0.3f),
    TORCH("torch", "Torch", FULL, false, 1f, 9999, 0.9f, true, 
        LightProperties(Color(1.0f, 0.7f, 0.3f), 120f, 1.3f, 0.15f)),
    LAVA("lava", "Lava", FULL, true, 1f, 9999, 0.7f, true, 
        LightProperties(Color(1.0f, 0.5f, 0.2f), 100f, 1.2f, 0.1f, 0.05f)),
    GLOWSTONE("glowstone", "Glowstone", FULL, true, 1f, 9999, 0.9f, true, 
        LightProperties(Color(1.0f, 1.0f, 0.8f), 150f, 1.5f));

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
    
    // Создает источник света для блока, если он излучает свет
    fun createLight(x: Float, y: Float): Light? {
        if (!emitsLight || lightProperties == null) return null
        
        return Light(
            x = x,
            y = y,
            radius = lightProperties.radius,
            falloff = lightProperties.falloff ?: 0.8f,
            color = lightProperties.color,
            intensity = lightProperties.intensity,
            flickerAmount = lightProperties.flickerAmount,
            colorShift = lightProperties.colorShift
        )
    }
}

// Класс для хранения свойств света блока
data class LightProperties(
    val color: Color,
    val radius: Float,
    val intensity: Float = 1.0f,
    val flickerAmount: Float = 0.0f,
    val colorShift: Float = 0.0f,
    val falloff: Float? = null
)