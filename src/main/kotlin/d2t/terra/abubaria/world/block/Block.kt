package d2t.terra.abubaria.world.block

import d2t.terra.abubaria.GamePanel.tileSizeF
import d2t.terra.abubaria.GamePanel.world
import d2t.terra.abubaria.event.BlockDestroyEvent
import d2t.terra.abubaria.event.BlockPlaceEvent
import d2t.terra.abubaria.event.EventService
import d2t.terra.abubaria.hitbox.BlockHitBox
import d2t.terra.abubaria.io.graphics.drawTexture
import d2t.terra.abubaria.light.Light
import d2t.terra.abubaria.light.LightManager
import d2t.terra.abubaria.world.lCount
import d2t.terra.abubaria.world.lSizeF
import d2t.terra.abubaria.world.lightLevels
import d2t.terra.abubaria.world.material.Material

class Block(
    private var material: Material = Material.AIR,
    var x: Int = 0,
    var y: Int = 0,
    var chunkX: Int = 0,
    var chunkY: Int = 0
) {
    var hitBox = BlockHitBox(this)
    var lightMap = Array(lCount) { Array(lCount) { Light(0f, 0f, 0, 0, 0, this) } }

    var type
        get() = material
        set(value) {
            material = value
            hitBox = BlockHitBox(this)
        }

    val lighted get() = lightMap.flatten().any { l -> l.power != lightLevels }

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

    fun draw(screenX: Float, screenY: Float) {
        drawTexture(type.texture?.textureId, screenX, screenY, tileSizeF, type.height)
    }

    fun initLightMap() {
        for (lightX in 0 until lCount) {
            for (lightY in 0 until lCount) {
                val light = Light(
                    lightX * lSizeF + x, lightY * lSizeF + y,
                    lightX, lightY, 0, this
                ).apply { initializePower() }
                lightMap[lightX][lightY] = light
            }
        }
    }

    fun updateLightAround() {
        for (dx in -lCount - 1..lCount + 1) {
            for (dy in -lCount - 1..lCount + 1) {
                val block = world.getBlockAt(x + dx, y + dy) ?: continue
                LightManager.forUpDate.add(this)
                LightManager.forUpDate.add(block)
            }
        }
    }
}