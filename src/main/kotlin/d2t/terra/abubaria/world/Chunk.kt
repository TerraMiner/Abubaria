package d2t.terra.abubaria.world

import d2t.terra.abubaria.Client
import d2t.terra.abubaria.chunkBitMask
import d2t.terra.abubaria.chunkSize
import d2t.terra.abubaria.geometry.box.BlockCollisionBox
import d2t.terra.abubaria.geometry.box.CollisionBox
import d2t.terra.abubaria.io.graphics.render.BatchSession
import d2t.terra.abubaria.tileSizeF
import d2t.terra.abubaria.util.loopWhile
import d2t.terra.abubaria.world.block.Block
import d2t.terra.abubaria.world.block.BlockInChunkPosition

class Chunk(
    val x: Int = 0,
    val y: Int = 0,
    val blockMap: Array<Block> = Array(chunkSize * chunkSize) { Block() }
) {
    val hitBox = CollisionBox(x.toFloat(), y.toFloat(), chunkSize * tileSizeF, chunkSize * tileSizeF)
    val worldSizeX = x * chunkSize
    val worldSizeY = y * chunkSize

//    val lighed get() = blockMap.flatten().any { it.lighted }

    fun initBlocks() {
        blockMap.forEachIndexed { index, block ->
            val position = BlockInChunkPosition(index.toByte())
            block.x = (x shl chunkBitMask) + position.x
            block.y = (y shl chunkBitMask) + position.y
            block.collisionBox = BlockCollisionBox(block)
        }
    }

    fun drawTextures(session: BatchSession) {
        blockMap.forEachIndexed { index, block ->
            block.drawTexture(session)
        }

//        if (Client.debugMode) {
//            val screenX = Camera.worldScreenPosX(worldSizeX * tileSize, location)
//            val screenY = Camera.worldScreenPosY(worldSizeY * tileSize, location)
//            drawString("x: $x, y: $y", screenX + 3, screenY + 14, 4, Color.BLACK)
//
//            safetyDraw(GL_LINE_LOOP) {
//                glLineWidth(1f)
//                drawRect(screenX, screenY, hitBox.width, hitBox.height)
//            }
//        }
    }

//    fun drawLights(location: Location) {
//        val worldSizeX = x * chunkSize
//        val worldSizeY = y * chunkSize
//
//        blockMap.forEachIndexed { index, block ->
//            val position = BlockInChunkPosition(index.toByte())
//            val screenX = Camera.worldScreenPosX((worldSizeX + position.x) * tileSize, location)
//            val screenY = Camera.worldScreenPosY((worldSizeY + position.y) * tileSize, location) + tileSizeF * block.type.state.offset
//            block.drawLight(screenX, screenY)
//        }
//    }


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
