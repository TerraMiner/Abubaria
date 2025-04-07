package d2t.terra.abubaria.world

import d2t.terra.abubaria.chunkShiftBits
import d2t.terra.abubaria.chunkSize
import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.geometry.box.BlockCollisionBox
import d2t.terra.abubaria.geometry.box.CollisionBox
import d2t.terra.abubaria.tileSizeF
import d2t.terra.abubaria.util.concurrentSetOf
import d2t.terra.abubaria.util.loopWhile
import d2t.terra.abubaria.world.block.Block
import d2t.terra.abubaria.world.block.BlockInChunkPosition

class Chunk(
    val x: Int = 0,
    val y: Int = 0,
    val blockMap: Array<Block> = Array(chunkSize * chunkSize) { Block() }
) {

    val entities = concurrentSetOf<Entity>()

    fun addEntity(entity: Entity) {
        entities.add(entity)
    }

    fun removeEntity(entity: Entity) {
        entities.remove(entity)
    }

    fun drawEntities() {
        entities.forEach(Entity::draw)
    }

    fun initBlocks() {
        blockMap.forEachIndexed { index, block ->
            val position = BlockInChunkPosition(index.toByte())
            block.x = (x shl chunkShiftBits) + position.x
            block.y = (y shl chunkShiftBits) + position.y
            block.collisionBox = BlockCollisionBox(block)
        }
    }

    fun drawBlocks() {
        blockMap.forEach(Block::drawTexture)
    }

    fun applyForBlocks(action: (x: Int, y: Int) -> Unit) {
        loopWhile(0, chunkSize) { x ->
            val worldX = x + this.x * chunkSize
            loopWhile(0, chunkSize) { y ->
            val worldY = y + this.y * chunkSize
                action(worldX, worldY)
            }
        }
    }
}
