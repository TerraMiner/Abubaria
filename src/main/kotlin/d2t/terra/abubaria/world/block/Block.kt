package d2t.terra.abubaria.world.block

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.particle.ParticleDestroy
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.light.Light
import d2t.terra.abubaria.hitbox.BlockHitBox
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.io.graphics.drawFillRect
import d2t.terra.abubaria.io.graphics.drawTexture
import d2t.terra.abubaria.world.lCount
import d2t.terra.abubaria.world.lSize
import d2t.terra.abubaria.world.material.Material

class Block(
    private var material: Material = Material.AIR,
    var x: Int = 0,
    var y: Int = 0,
    var chunkX: Int = 0,
    var chunkY: Int = 0
) {
    var hitBox = BlockHitBox(this)
    val world = GamePanel.world
    var lightMap = Array(lCount) { Array(lCount) { Light(0, 0, 0, 0, 0, this) } }

    var type
        get() = material
        set(value) {
            material = value
            hitBox = BlockHitBox(this)
        }

    fun destroy() {
        if (type === Material.AIR) return
        ParticleDestroy(this).initParticles()
        type = Material.AIR
    }

    fun getBlockAt(dx: Int, dy: Int) = world.getBlockAt(x + dx, y + dy)

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
        for (i in 0 until lCount) {
            around.add(getBlockAt(i, i))
            around.add(getBlockAt(i, -i))
            around.add(getBlockAt(-i, -i))
            around.add(getBlockAt(-i, i))
        }

        around.filterNotNull().forEach { block ->
            block.lightMap.flatten().forEach {
                it.initializePower()
            }
        }
    }
}