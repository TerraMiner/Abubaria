package d2t.terra.abubaria.world

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.blockBitMask
import d2t.terra.abubaria.chunkBitMask
import d2t.terra.abubaria.chunkSize
import d2t.terra.abubaria.tileSize
import d2t.terra.abubaria.entity.EntityService
import d2t.terra.abubaria.geometry.box.BlockCollisionBox
import d2t.terra.abubaria.geometry.box.ColliderType
import d2t.terra.abubaria.geometry.box.CollisionBox
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.util.getIndex
import d2t.terra.abubaria.util.loopIndicy
import d2t.terra.abubaria.world.block.Block
import d2t.terra.abubaria.world.block.BlockInChunkPosition
import d2t.terra.abubaria.world.block.Position
import d2t.terra.abubaria.world.material.Material
import d2t.terra.abubaria.io.graphics.render.BatchRenderer
import d2t.terra.abubaria.io.graphics.render.BatchSession
import d2t.terra.abubaria.io.graphics.render.RendererManager

class World {
    val worldChunkWidth = 16
    val worldChunkHeight = 16

    val width = tileSize * chunkSize * worldChunkWidth
    val height = tileSize * chunkSize * worldChunkHeight

    val spawnLocation = Location(width / 2f, tileSize * 10f, Direction.LEFT, this)

    val worldBorder =
        CollisionBox(0f, 0f, width.toFloat(), height.toFloat()).apply { colliderType = ColliderType.INSIDE }

    val chunkMap = Array(worldChunkWidth * worldChunkHeight) { Chunk() }

    fun getChunk(x: Int, y: Int): Chunk? = chunkMap.getOrNull(getIndex(x, y, worldChunkWidth, worldChunkHeight))

    fun getChunkAt(x: Int, y: Int): Chunk? {
        return chunkMap.getOrNull(getIndex(x shr chunkBitMask, y shr chunkBitMask, worldChunkWidth, worldChunkHeight))
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
            it.collisionBox = BlockCollisionBox(it)
        }
    }

    fun generateWorldLight() {
        chunkMap.forEach { chunk ->
            chunk.applyForBlocks { x, y ->
                val block = GamePanel.world.getBlockAt(x, y) ?: return@applyForBlocks
                block.initLightMap()
            }
        }
    }

    fun draw(session: BatchSession) {
        val minVisibleChunkX = (((-Camera.cameraX + tileSize * 2) / tileSize) / chunkSize - 1).toInt()
        val minVisibleChunkY = (((-Camera.cameraY + tileSize * 2) / tileSize) / chunkSize - 1).toInt()
        val maxVisibleChunkX = (((-Camera.cameraX + Window.width + tileSize * 2) / tileSize) / chunkSize).toInt()
        val maxVisibleChunkY = (((-Camera.cameraY + Window.height + tileSize * 2) / tileSize) / chunkSize).toInt()

        val posX = (-Camera.cameraX / tileSize / chunkSize).toInt()
        val posY = (-Camera.cameraY / tileSize / chunkSize).toInt()

        val entityLevel = EntityService.EntitiesByPositions[GamePanel.world]

        loopIndicy(minVisibleChunkX, maxVisibleChunkX) { chunkX ->
            val worldChunkX = chunkX - posX
            if (worldChunkX < 0 || worldChunkX > worldChunkWidth) return@loopIndicy

            loopIndicy(minVisibleChunkY, maxVisibleChunkY) { chunkY ->
                val worldChunkY = chunkY - posY
                if (worldChunkY < 0 || worldChunkY > worldChunkHeight) return@loopIndicy
                getChunk(chunkX, chunkY)?.drawTextures(session)
                entityLevel?.getSection(Position(chunkX, chunkY))?.drawEntities(session)
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
}