package d2t.terra.abubaria.world

import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.GamePanel.tileSizeF
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.hitbox.BlockHitBox
import d2t.terra.abubaria.hitbox.HitBox
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.util.loopWhile
import d2t.terra.abubaria.world.block.Block
import d2t.terra.abubaria.world.block.BlockInChunkPosition

class Chunk(
    val x: Int = 0,
    val y: Int = 0,
    val blockMap: Array<Block> = Array(chunkSize * chunkSize) { Block() }
) {
    val hitBox = HitBox(x, y, chunkSize * tileSize, chunkSize * tileSize)
    val worldSizeX = x * chunkSize
    val worldSizeY = y * chunkSize
    //block in world size = x * chunkSize + blockX

//    val lighed get() = blockMap.flatten().any { it.lighted }

    fun initBlocks() {
//        blockMap.forEachIndexed { x, blocks ->
//            blocks.forEachIndexed { y, block ->
//                block.x = this.x * chunkSize + x
//                block.y = this.y * chunkSize + y
//                block.chunkX = this.x
//                block.chunkY = this.y
//                block.hitBox = BlockHitBox(block)
//            }
//        }
        blockMap.forEachIndexed { index, block ->
            val position = BlockInChunkPosition(index.toByte())
            block.x = (x shl chunkBitMask) + position.x
            block.y = (y shl chunkBitMask) + position.y
            block.hitBox = BlockHitBox(block)
        }
    }

    fun drawTextures() {
        blockMap.forEachIndexed { index, block ->
            block.drawTexture()
        }

//        if (Client.debugMode) {
//            val screenX = Camera.worldScreenPosX(worldSizeX * tileSize, location)
//            val screenY = Camera.worldScreenPosY(worldSizeY * tileSize, location)
//            drawString("x: $x, y: $y", screenX + 3, screenY + 14, 4, Color.BLACK)

//            safetyDraw(GL_LINE_LOOP) {
//                glLineWidth(1f)
//                drawRect(screenX, screenY, hitBox.width, hitBox.height)
//            }
//        }
    }

//    fun drawTextures(location: Location, a: DebugCont) {
//        blockMap.forEachIndexed { index, block ->
//
//            val position = BlockInChunkPosition(index.toByte())
//            val screenX = Camera.worldScreenPosX((worldSizeX + position.x) * tileSize, location)
//            val screenY = Camera.worldScreenPosY((worldSizeY + position.y) * tileSize, location) + tileSizeF * block.type.state.offset
//            block.drawTexture(screenX, screenY)
//            if (!a.debugged) {
//                println("screenX: $screenX, screenY: $screenY")
//                a.debugged = true
//            }
//        }
//
////        if (Client.debugMode) {
////            val screenX = Camera.worldScreenPosX(worldSizeX * tileSize, location)
////            val screenY = Camera.worldScreenPosY(worldSizeY * tileSize, location)
////            drawString("x: $x, y: $y", screenX + 3, screenY + 14, 4, Color.BLACK)
//
////            safetyDraw(GL_LINE_LOOP) {
////                glLineWidth(1f)
////                drawRect(screenX, screenY, hitBox.width, hitBox.height)
////            }
////        }
//    }

    fun drawLights(location: Location) {
        val worldSizeX = x * chunkSize
        val worldSizeY = y * chunkSize

        blockMap.forEachIndexed { index, block ->
            val position = BlockInChunkPosition(index.toByte())
            val screenX = Camera.worldScreenPosX((worldSizeX + position.x) * tileSize, location)
            val screenY = Camera.worldScreenPosY((worldSizeY + position.y) * tileSize, location) + tileSizeF * block.type.state.offset
            block.drawLight(screenX, screenY)
        }
    }


    fun applyForBlocks(action: (x: Int, y: Int) -> Unit) {
        loopWhile(0,chunkSize) { x ->
            val worldX = x + this.x * chunkSize
            loopWhile(0,chunkSize) { y ->
            val worldY = y + this.y * chunkSize
                action(worldX, worldY)
            }
        }
    }

    companion object {
        fun getCoords(inChunkId: Short): Pair<Int, Int> {
            val x = (inChunkId.toInt() shr 3) and 0x7
            val y = inChunkId.toInt() and 0x7
            return Pair(x, y)
        }

        val Pair<Int, Int>.inChunkId get() = (((first and 0x7) shl 3) or (second and 0x7)).toShort()
    }
}
