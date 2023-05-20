package d2t.terra.abubaria.world

import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.hitbox.BlockHitBox
import d2t.terra.abubaria.hitbox.HitBox
import d2t.terra.abubaria.io.graphics.drawRect
import d2t.terra.abubaria.io.graphics.safetyDraw
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.block.Block
import d2t.terra.abubaria.world.material.Material
import org.lwjgl.opengl.GL11.GL_LINE_LOOP
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.abs

class World {

    val worldSizeX = 16
    val worldSizeY = 16

    val worldWidth = tileSize * chunkSize * worldSizeX
    val worldHeight = tileSize * chunkSize * worldSizeY

    val worldBorder = HitBox(0, 0, worldWidth, worldHeight)

    val chunkMap = Array(worldSizeX) { Array(worldSizeY) { Chunk() } }

    val entities = ConcurrentLinkedQueue<Entity>()

    fun getChunkAt(x: Int, y: Int): Chunk? {
        return chunkMap.getOrNull(x / chunkSize)
            ?.getOrNull(y / chunkSize)
    }

    fun getBlockAt(x: Int, y: Int): Block? {
        val chunk = getChunkAt(x, y) ?: return null
        return chunk.blockMap.getOrNull(x - chunk.x * chunkSize)?.getOrNull(y - chunk.y * chunkSize)
    }

    fun setBlock(material: Material, x: Int, y: Int) {
        getBlockAt(x, y)?.also {
            it.type = material
            it.hitBox = BlockHitBox(it)
        }
    }

    fun generateWorldLight() {
        chunkMap.flatten().forEach { chunk ->
            chunk.applyForBlocks { x, y ->
                val block = GamePanel.world.getBlockAt(x, y) ?: return@applyForBlocks
                block.initLightMap()
            }
        }
    }

    fun draw(location: Location) {
        val extraDrawDistX = abs(Camera.playerScreenPosX(location) - Camera.cameraX) + tileSize * chunkSize
        val extraDrawDistY = abs(Camera.playerScreenPosY(location) - Camera.cameraY) + tileSize * chunkSize

        val leftVertex = Camera.leftCameraX(location) - extraDrawDistX
        val rightVertex = Camera.rightCameraX(location) + extraDrawDistX
        val bottomVertex = Camera.bottomCameraY(location) + extraDrawDistY
        val topVertex = Camera.topCameraY(location) - extraDrawDistY

        val chunks = mutableListOf<Chunk>()

        val leftCorner = (leftVertex / tileSize / chunkSize).toInt()
            .coerceIn(0 until worldSizeX)
        val rightCorner = (rightVertex / tileSize / chunkSize).toInt()
            .coerceIn(0 until worldSizeX)
        val bottomCorner = (bottomVertex / tileSize / chunkSize).toInt()
            .coerceIn(0 until worldSizeY)
        val topCorner = (topVertex / tileSize / chunkSize).toInt()
            .coerceIn(0 until worldSizeY)

        for (chunkX in leftCorner..rightCorner) {
            for (chunkY in topCorner..bottomCorner) {
                val chunk = chunkMap[chunkX][chunkY]
                chunk.draw(location)
                chunks.add(chunk)
            }
        }

        drawEntities(location, leftVertex, rightVertex, topVertex, bottomVertex)

        Camera.chunksOnScreen = chunks
    }

    private fun drawEntities(location: Location, lVertex: Float, rVertex: Float, tVertex: Float, bVertex: Float) {
        entities.filter {
            it.location.run { x in lVertex..rVertex && y in tVertex..bVertex }
        }.apply {
            forEach {
                it.draw(location)
            }

            if (Client.debugMode) {
                safetyDraw(GL_LINE_LOOP) {
                    forEach {
                        it.hitBox.draw(location)
                    }
                }
            }
        }
    }


//    private fun drawEntitiesHitBoxes(location: Location, lVertex: Double, rVertex: Double, tVertex: Double, bVertex: Double) {
//        if (Client.debugMode) {
//            entities.filter {
//                it.location.run { x in lVertex..rVertex && y in tVertex..bVertex }
//            }.forEach {
//                it.drawHitBox(location)
//            }
//        }
//    }


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