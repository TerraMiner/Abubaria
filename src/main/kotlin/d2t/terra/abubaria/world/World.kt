package d2t.terra.abubaria.world

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.GamePanel.tileSizeF
import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.entity.item.EntityItem
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.hitbox.BlockHitBox
import d2t.terra.abubaria.hitbox.HitBox
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.util.loopIndicy
import d2t.terra.abubaria.world.block.Block
import d2t.terra.abubaria.world.block.BlockInChunkPosition
import d2t.terra.abubaria.world.block.Position
import d2t.terra.abubaria.world.material.Material
import java.util.concurrent.ConcurrentLinkedQueue

class World {


    val worldChunkWidth = 16
    val worldChunkHeight = 16

    val width = tileSize * chunkSize * worldChunkWidth
    val height = tileSize * chunkSize * worldChunkHeight

    val spawnLocation = Location(width / 2F, tileSize * 10F, Direction.LEFT)

    val worldBorder = HitBox(0, 0, width, height)

    val chunkMap = Array(worldChunkWidth) { Array(worldChunkHeight) { Chunk() } }//todo indexes file

    val entities = ConcurrentLinkedQueue<Entity>()

    fun getChunk(x: Int, y: Int): Chunk? = chunkMap.getOrNull(x)?.getOrNull(y)

    fun getChunkAt(x: Int, y: Int): Chunk? {
        return chunkMap.getOrNull(x shr chunkBitMask)
            ?.getOrNull(y shr chunkBitMask)
    }

    fun getBlockAt(pos: Position) = getBlockAt(pos.x, pos.y)

    fun getBlockAt(x: Int, y: Int): Block? {
        val chunk = getChunkAt(x, y) ?: return null
        val position = BlockInChunkPosition.decode(x and blockBitMask, y and blockBitMask)
        return chunk.blockMap.getOrNull(position.value.toInt())
    }

    fun setBlock(material: Material, x: Int, y: Int) {
        getBlockAt(x, y)?.also {
            it.type = material
            it.hitBox = BlockHitBox(it)
        }
    }

    fun generateWorldLight() {
        chunkMap.forEach { chunkCol ->
            chunkCol.forEach { chunk ->
                chunk.applyForBlocks { x, y ->
                    val block = GamePanel.world.getBlockAt(x, y) ?: return@applyForBlocks
                    block.initLightMap()
                }
            }
        }
    }

    fun draw() {
        val minVisibleChunkX = (((-Camera.cameraX + tileSize * 2f) / tileSize) / chunkSize - 1).toInt()
        val minVisibleChunkY = (((-Camera.cameraY + tileSize * 2f) / tileSize) / chunkSize - 1).toInt()
        val maxVisibleChunkX = (((-Camera.cameraX + Window.width + tileSize * 2f) / tileSize) / chunkSize).toInt()
        val maxVisibleChunkY = (((-Camera.cameraY + Window.height + tileSize * 2f) / tileSize) / chunkSize).toInt()

        val posX = (-Camera.cameraX / tileSize / chunkSize).toInt()
        val posY = (-Camera.cameraY / tileSize / chunkSize).toInt()

        loopIndicy(minVisibleChunkX,maxVisibleChunkX) { chunkX ->
            val worldChunkX = chunkX - posX
            if (worldChunkX < 0 || worldChunkX > width) return@loopIndicy

            loopIndicy (minVisibleChunkY,maxVisibleChunkY) { chunkY->
                val worldChunkY = chunkY - posY
                if (worldChunkY < 0 || worldChunkY > height) return@loopIndicy
                getChunk(chunkX, chunkY)?.drawTextures()
            }
        }


//        if (Client.lightMode) {
//            safetyDraw(GL_QUADS) {
//                for (chunkX in leftCorner..rightCorner) {
//                    for (chunkY in topCorner..bottomCorner) {
//                        val chunk = chunkMap[chunkX][chunkY]
//                        chunk.drawLights(location)
//                    }
//                }
//            }
//        }

//        drawEntities(minVisibleChunkX, maxVisibleChunkX, minVisibleChunkY, maxVisibleChunkY)
    }

    private fun drawEntities(minVisibleChunkX: Int, maxVisibleChunkX: Int, minVisibleChunkY: Int, maxVisibleChunkY: Int) {
        entities.forEach {
            val chunkCoordX = (it.location.x / tileSizeF).toInt() shr chunkBitMask
            val chunkCoordY = (it.location.y / tileSizeF).toInt() shr chunkBitMask
            if (chunkCoordX < minVisibleChunkX || chunkCoordX > maxVisibleChunkX ||
                chunkCoordY < minVisibleChunkY || chunkCoordY > maxVisibleChunkY) return@forEach
            it.draw()
        }

//            if (Client.debugMode) {
//                safetyDraw(GL_LINE_LOOP) {
//                    forEach {
//                        it.hitBox.draw(location)
//                    }
//                }
//            }
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
        }

        entities.removeIf(Entity::removed)
    }
}