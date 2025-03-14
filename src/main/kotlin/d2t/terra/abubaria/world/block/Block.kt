package d2t.terra.abubaria.world.block

import d2t.terra.abubaria.GamePanel.tileSizeF
import d2t.terra.abubaria.GamePanel.world
import d2t.terra.abubaria.event.BlockDestroyEvent
import d2t.terra.abubaria.event.BlockPlaceEvent
import d2t.terra.abubaria.event.EventService
import d2t.terra.abubaria.hitbox.BlockHitBox
import d2t.terra.abubaria.io.graphics.render.RendererManager
import d2t.terra.abubaria.light.Light
import d2t.terra.abubaria.light.LightInBlockPosition
import d2t.terra.abubaria.light.LightManager
import d2t.terra.abubaria.world.lCount
import d2t.terra.abubaria.world.lSize
import d2t.terra.abubaria.world.lSizeF
import d2t.terra.abubaria.world.material.Material
import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.util.loopIndicy

class Block(
    private var material: Material = Material.AIR,
    val pos: Position = Position(0, 0)
) {
    var x by pos::x
    var y by pos::y

    val chunkX get() = pos.chunkX
    val chunkY get() = pos.chunkY

    var hitBox = BlockHitBox(this)
    var lightMap = Array(lCount * lCount) { Light(LightInBlockPosition(it.toByte()), pos) }

    var type
        get() = material
        set(value) {
            material = value
            hitBox = BlockHitBox(this)
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


    fun relative(blockFace: BlockFace): Block? {

        return when (blockFace) {
            BlockFace.DOWN -> {
                world.getBlockAt(x, y + 1)
            }

            BlockFace.UP -> {
                world.getBlockAt(x, y - 1)
            }

            BlockFace.LEFT -> {
                world.getBlockAt(x - 1, y)
            }

            BlockFace.RIGHT -> {
                world.getBlockAt(x + 1, y)
            }
        }
    }

    fun drawTexture() {
        val texture = type.texture ?: return
        RendererManager.WorldRenderer.render(
            texture,
            Model.DEFAULT,
            x.toFloat() * tileSizeF,
            y.toFloat() * tileSizeF + tileSizeF * type.state.offset,
            tileSizeF,
            tileSizeF * type.scale
        )
    }

    fun drawLight(screenX: Float, screenY: Float) {
//        if (type !== Material.AIR) {
//            lightMap.forEach { light ->
//                drawFillRect(
//                    screenX + light.lightPos.x * lSize,
//                    screenY + light.lightPos.y * lSize,
//                    lSizeF, lSizeF, light.power * 16
//                )
//            }
//        }
    }

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