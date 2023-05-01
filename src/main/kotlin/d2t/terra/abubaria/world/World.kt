package d2t.terra.abubaria.world

import LagDebugger
import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.Entity
//import d2t.terra.abubaria.entity.Particle
//import d2t.terra.abubaria.entity.ParticleDestroy
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.location.BlockHitBox
import d2t.terra.abubaria.location.HitBox
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.tile.Material
import d2t.terra.abubaria.world.tile.Material.*
import lwjgl.drawTexture
import java.awt.Color
import java.awt.Graphics2D
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.abs

enum class BlockFace {
    DOWN, UP, LEFT, RIGHT
}

class Block(private var material: Material = AIR, var x: Int = 0, var y: Int = 0, var chunkX: Int = 0, var chunkY: Int = 0) {
    var hitBox = BlockHitBox(this)
    val world = GamePanel.world

    var type get() = material
        set(value) {
            material = value
            hitBox = BlockHitBox(this)
        }

    fun destroy() {
//        ParticleDestroy(this)
        type = AIR
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
}

class Chunk(
    val x: Int = 0,
    val y: Int = 0,
    val blocks: Array<Array<Block>> = Array(chunkSize) { Array(chunkSize) { Block() } }
) {
    val hitBox = HitBox(x, y, chunkSize * tileSize, chunkSize * tileSize)
    //block in world size = x * chunkSize + blockX

    fun initBlocks() {
        blocks.forEachIndexed { x, blocks ->
            blocks.forEachIndexed { y, block ->
                block.x = this.x * chunkSize + x
                block.y = this.y * chunkSize + y
                block.chunkX = this.x
                block.chunkY = this.y
                block.hitBox = BlockHitBox(block)
            }
        }
    }
}

const val chunkSize = 8

class World {

    val worldSizeX = 16
    val worldSizeY = 16

    val worldWidth = tileSize * chunkSize * worldSizeX
    val worldHeight = tileSize * chunkSize * worldSizeY

    val worldBorder = HitBox(0, 0, worldWidth, worldHeight)

    val chunks = Array(worldSizeX) { Array(worldSizeY) { Chunk() } }

    val entities = ConcurrentLinkedQueue<Entity>()

    fun getChunkAt(x: Int, y: Int): Chunk? {
        return chunks.getOrNull(x / chunkSize)
            ?.getOrNull(y / chunkSize)
    }

    fun getBlockAt(x: Int, y: Int): Block? {
        val chunk = getChunkAt(x, y) ?: return null

        return chunk.blocks.getOrNull(x - chunk.x * chunkSize)?.getOrNull(y - chunk.y * chunkSize)
    }

    fun setBlock(material: Material, x: Int, y: Int) {
        getBlockAt(x, y)?.also {
            it.type = material
            it.hitBox = BlockHitBox(it)
        }
    }

    fun draw(location: Location) {

        val a = LagDebugger()
        a.enabled = false
        a.check(121)
        val extraDrawDistX = abs((Camera.playerScreenPosX(location) - Camera.screenX) / tileSize / chunkSize) + 1
        val extraDrawDistY = abs((Camera.playerScreenPosY(location) - Camera.screenY) / tileSize / chunkSize) + 1

        var rightCorner = (Camera.offsetX(location) / tileSize / chunkSize).toInt() + extraDrawDistX
        var leftCorner = (Camera.onsetX(location) / tileSize / chunkSize).toInt() - extraDrawDistX
        var bottomCorner = (Camera.offsetY(location) / tileSize / chunkSize).toInt() + extraDrawDistY
        var topCorner = (Camera.onsetY(location) / tileSize / chunkSize).toInt() - extraDrawDistY

        leftCorner = if (leftCorner < 0) 0 else if (leftCorner >= worldSizeX) worldSizeX - 1 else leftCorner
        rightCorner = if (rightCorner < 0) 0 else if (rightCorner >= worldSizeX) worldSizeX - 1 else rightCorner
        bottomCorner = if (bottomCorner < 0) 0 else if (bottomCorner >= worldSizeY) worldSizeY - 1 else bottomCorner
        topCorner = if (topCorner < 0) 0 else if (topCorner >= worldSizeY) worldSizeY - 1 else topCorner
        a.check(134)

        for (chunkX in leftCorner..rightCorner) {
            for (chunkY in topCorner..bottomCorner) {
                chunks[chunkX][chunkY].draw(location)
//                entities.forEach {
//                    it.draw(location)
//                }
            }
        }
        a.check(144)
        a.debug("world")
    }

    private fun Chunk.draw(location: Location) {
        blocks.forEachIndexed { x, blockCols ->
            val worldX = (this.x * chunkSize + x) * tileSize
            blockCols.forEachIndexed { y, block ->
                val worldY = (this.y * chunkSize + y) * tileSize
                block.draw(worldX, worldY, location)
            }
        }

//        if (Client.debugMode) {
//            val prevColor = g2.color
//            g2.color = Color.BLACK
//
//            val screenX = Camera.worldScreenPosX(x * tileSize * chunkSize, location)
//            val screenY = Camera.worldScreenPosY(y * tileSize * chunkSize, location)
//
//            g2.drawRect(screenX, screenY, hitBox.width.toInt(), hitBox.height.toInt())
//
//            g2.drawString("x: $x, y: $y", screenX + 3, screenY + 14)
//            g2.color = prevColor
//        }
    }


    private fun Block.draw(worldX: Int, worldY: Int, location: Location) {
        val screenX = Camera.worldScreenPosX(worldX, location)
        val screenY = Camera.worldScreenPosY(worldY, location)
        drawTexture(type.texture, screenX, screenY + type.state.offset, tileSize, tileSize)
//        g2.drawImage(type.texture, screenX, screenY + type.state.offset, null)
    }

//    fun update() {
//        entities.forEach {
//
//            when {
//                it is ParticleDestroy -> {
//
//                    if (it.removed) {
//                        entities.remove(it)
//                    }
//
//                    it.update()
//                }
//
//                it is Particle -> {
//                    if (it.removed) entities.remove(it)
//                }
//            }
//        }
//    }
}