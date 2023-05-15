package d2t.terra.abubaria.world.block

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.world
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.event.BlockDestroyEvent
import d2t.terra.abubaria.event.BlockPlaceEvent
import d2t.terra.abubaria.event.EventService
import d2t.terra.abubaria.hitbox.BlockHitBox
import d2t.terra.abubaria.io.graphics.drawTexture
import d2t.terra.abubaria.light.Light
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.lCount
import d2t.terra.abubaria.world.lSize
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
    var lightMap = Array(lCount) { Array(lCount) { Light(0, 0, 0, 0, 0, this) } }

    var type
        get() = material
        set(value) {
            material = value
            hitBox = BlockHitBox(this)
        }

    val fullShadowed get() = lightMap.flatten().none { l -> l.power != lightLevels }

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

    fun draw(worldX: Int, worldY: Int, location: Location) {
        if (fullShadowed) return
        val screenX = Camera.worldScreenPosX(worldX, location)
        val screenY = (Camera.worldScreenPosY(worldY, location) + (GamePanel.tileSize * type.state.offset).toInt())

        drawTexture(type.texture?.textureId, screenX, screenY, GamePanel.tileSize, type.height)
    }

    fun initLightMap() {
        for (lightX in 0 until lCount) {
            for (lightY in 0 until lCount) {
                val light = Light(
                    lightX * lSize + x, lightY * lSize + y,
                    lightX, lightY, 0, this
                ).apply { initializePower() }
                lightMap[lightX][lightY] = light
            }
        }
    }

    fun updateLightAround() {
        val around = mutableListOf<Block?>()
        for (dx in -lCount-1 .. lCount+1) {
            for (dy in -lCount-1 .. lCount+1) {
                around.add(this)
                around.add(world.getBlockAt(x + dx, y + dy))
            }
        }

        around.filterNotNull().forEach { block ->
            block.lightMap.flatten().forEach {
                it.initializePower()
            }
        }
    }
}