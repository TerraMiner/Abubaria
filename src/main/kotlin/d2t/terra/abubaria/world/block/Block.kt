package d2t.terra.abubaria.world.block

import d2t.terra.abubaria.tileSizeF
import d2t.terra.abubaria.GamePanel.world
import d2t.terra.abubaria.event.BlockDestroyEvent
import d2t.terra.abubaria.event.BlockPlaceEvent
import d2t.terra.abubaria.event.EventService
import d2t.terra.abubaria.geometry.box.BlockCollisionBox
import d2t.terra.abubaria.light.Light
import d2t.terra.abubaria.light.LightInBlockPosition
import d2t.terra.abubaria.light.LightManager
import d2t.terra.abubaria.lCount
import d2t.terra.abubaria.world.material.Material
import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.render.RenderDimension
import d2t.terra.abubaria.io.graphics.render.Renderer
import d2t.terra.abubaria.io.graphics.render.WORLD_BLOCKS_LAYER
import d2t.terra.abubaria.util.loopIndicy

class Block(
    private var material: Material = Material.AIR,
    val pos: Position = Position(0, 0)
) {
    var x by pos::x
    var y by pos::y

    val chunkX get() = pos.chunkX
    val chunkY get() = pos.chunkY

    var collisionBox = BlockCollisionBox(this)
    var lightMap = Array(lCount * lCount) { Light(LightInBlockPosition(it.toByte()), pos) }

    var type
        get() = material
        set(value) {
            material = value
            collisionBox = BlockCollisionBox(this)
        }

//    val lighted get() = lightMap.flatten().any { l -> l.power != lightLevels }

    fun destroy() {
        if (type === Material.AIR) return
        EventService.launch(BlockDestroyEvent(this))
        type = Material.AIR
    }

    fun place(type: Material) {
        if (type === Material.AIR) return
        this.type = type
        EventService.launch(BlockPlaceEvent(this))
    }


    fun relative(blockFace: BlockFace, dist: Int): Block? {

        return when (blockFace) {
            BlockFace.BOTTOM -> {
                world.getBlockAt(x, y + dist)
            }

            BlockFace.TOP -> {
                world.getBlockAt(x, y - dist)
            }

            BlockFace.LEFT -> {
                world.getBlockAt(x - dist, y)
            }

            BlockFace.RIGHT -> {
                world.getBlockAt(x + dist, y)
            }
        }
    }

    fun drawTexture() {
        val texture = type.texture ?: return
        Renderer.render(
            texture,
            Model.DEFAULT,
            x * tileSizeF,
            y * tileSizeF + type.state.offset * tileSizeF,
            tileSizeF,
            tileSizeF * type.scale,
            zIndex = WORLD_BLOCKS_LAYER,
            dim = RenderDimension.WORLD,
            ignoreCamera = false
        )
    }

//    fun drawLight(screenX: Float, screenY: Float) {
//        if (type !== Material.AIR) {
//            lightMap.forEach { light ->
//                drawFillRect(
//                    screenX + light.lightPos.x * lSize,
//                    screenY + light.lightPos.y * lSize,
//                    lSizeF, lSizeF, light.power * 16
//                )
//            }
//        }
//    }

    fun initLightMap() {
        lightMap.forEach(Light::initializePower)
    }

    fun updateLightAround() {
        LightManager.forUpDate.add(this)

        loopIndicy(-lCount - 1, lCount + 1) { dx ->
            loopIndicy(-lCount - 1,lCount + 1) { dy ->
                val block = world.getBlockAt(x + dx, y + dy) ?: return@loopIndicy
                LightManager.forUpDate.add(block)
            }
        }
    }
}