package d2t.terra.abubaria.world

import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.entity.ParticleDestroy
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.location.BlockHitBox
import d2t.terra.abubaria.location.HitBox
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.lwjgl.drawRect
import d2t.terra.abubaria.lwjgl.drawString
import d2t.terra.abubaria.lwjgl.drawTexture
import d2t.terra.abubaria.world.tile.Material
import d2t.terra.abubaria.world.tile.Material.AIR
import java.awt.Color
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.abs

enum class BlockFace {
    DOWN, UP, LEFT, RIGHT
}

class Block(
    private var material: Material = AIR,
    var x: Int = 0,
    var y: Int = 0,
    var chunkX: Int = 0,
    var chunkY: Int = 0
) {
    var hitBox = BlockHitBox(this)
    val world = GamePanel.world

    var type
        get() = material
        set(value) {
            material = value
            hitBox = BlockHitBox(this)
        }

    fun destroy() {
        if (type === AIR) return
        ParticleDestroy(this).initParticles()
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
        val extraDrawDistX = abs((Camera.playerScreenPosX(location) - Camera.cameraX) / tileSize / chunkSize) + 1
        val extraDrawDistY = abs((Camera.playerScreenPosY(location) - Camera.cameraY) / tileSize / chunkSize) + 1

        val leftVertex = Camera.leftCameraX(location)
        val rightVertex = Camera.rightCameraX(location)
        val bottomVertex = Camera.bottomCameraY(location)
        val topVertex = Camera.topCameraY(location)

        val leftCorner = ((leftVertex / tileSize / chunkSize).toInt() - extraDrawDistX)
            .coerceIn(0 until worldSizeX)
        val rightCorner = ((rightVertex / tileSize / chunkSize).toInt() + extraDrawDistX)
            .coerceIn(0 until worldSizeX)
        val bottomCorner = ((bottomVertex / tileSize / chunkSize).toInt() + extraDrawDistY)
            .coerceIn(0 until worldSizeY)
        val topCorner = ((topVertex / tileSize / chunkSize).toInt() - extraDrawDistY)
            .coerceIn(0 until worldSizeY)

        for (chunkX in leftCorner..rightCorner) {
            for (chunkY in topCorner..bottomCorner) {
                chunks[chunkX][chunkY].draw(location)
                drawEntities(location,leftVertex,rightVertex,topVertex,bottomVertex)
            }
        }
    }

    private fun drawEntities(location: Location, leftVertex: Double, rightVertex: Double, topVertex: Double, bottomVertex: Double) {
        entities.filter {
            it.location.run {
                x in leftVertex..rightVertex &&
                y in topVertex..bottomVertex }
        }.forEach {
            it.draw(location)
        }
    }

    private fun Chunk.draw(location: Location) {
        blocks.forEachIndexed { x, blockCols ->
            val worldX = (this.x * chunkSize + x) * tileSize
            blockCols.forEachIndexed { y, block ->
                val worldY = (this.y * chunkSize + y) * tileSize
                block.draw(worldX, worldY, location)
            }
        }

        if (Client.debugMode) {
            val screenX = Camera.worldScreenPosX(x * tileSize * chunkSize, location)
            val screenY = Camera.worldScreenPosY(y * tileSize * chunkSize, location)

            drawRect(screenX, screenY, hitBox.width.toInt(), hitBox.height.toInt(), 1f, Color.GRAY)

            drawString("x: $x, y: $y", screenX + 3, screenY + 14, 4, Color.GRAY)
        }
    }


    private fun Block.draw(worldX: Int, worldY: Int, location: Location) {
        val screenX = Camera.worldScreenPosX(worldX, location)
        val screenY = (Camera.worldScreenPosY(worldY, location) + (tileSize * type.state.offset).toInt())

        drawTexture(type.texture?.textureId, screenX, screenY, tileSize, type.height)
    }

    fun update() {
        updateEntities()
    }

    private fun updateEntities() {
        entities.forEach {
            it.update()
            if (it.removed) {
                entities.remove(it)
            }
        }
    }
}