package d2t.terra.abubaria.world

import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.hitbox.BlockHitBox
import d2t.terra.abubaria.hitbox.HitBox
import d2t.terra.abubaria.io.graphics.drawString
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.block.Block
import java.awt.Color

class Chunk(
    val x: Int = 0,
    val y: Int = 0,
    val blockMap: Array<Array<Block>> = Array(chunkSize) { Array(chunkSize) { Block() } }
) {
    val hitBox = HitBox(x, y, chunkSize * tileSize, chunkSize * tileSize)
    //block in world size = x * chunkSize + blockX

    val fullShadowed get() = blockMap.flatten().none { !it.fullShadowed }

    fun initBlocks() {
        blockMap.forEachIndexed { x, blocks ->
            blocks.forEachIndexed { y, block ->
                block.x = this.x * chunkSize + x
                block.y = this.y * chunkSize + y
                block.chunkX = this.x
                block.chunkY = this.y
                block.hitBox = BlockHitBox(block)
            }
        }
    }

    fun draw(location: Location) {
        if (fullShadowed) return
        blockMap.forEachIndexed { x, blockCols ->
            val worldX = (this.x * chunkSize + x) * tileSize
            blockCols.forEachIndexed { y, block ->
                val worldY = (this.y * chunkSize + y) * tileSize
                block.draw(worldX, worldY, location)

            }
        }

        if (Client.debugMode) {
            val screenX = Camera.worldScreenPosX(x * tileSize * chunkSize, location)
            val screenY = Camera.worldScreenPosY(y * tileSize * chunkSize, location)
            drawString("x: $x, y: $y", screenX + 3, screenY + 14, 4, Color.GRAY)
        }
    }

//    fun drawHitBoxes(location: Location) {
//        if (Client.debugMode) {
//            val screenX = Camera.worldScreenPosX(x * tileSize * chunkSize, location)
//            val screenY = Camera.worldScreenPosY(y * tileSize * chunkSize, location)
//            drawRect(screenX, screenY, hitBox.width.toInt(), hitBox.height.toInt(), Color.GRAY)
//        }
//    }

    fun applyForBlocks(action: (x: Int, y: Int) -> Unit) {
        for (x in 0 until chunkSize) {
            val worldX = x + this.x * chunkSize
            for (y in 0 until chunkSize) {
                val worldY = y + this.y * chunkSize
                action(worldX, worldY)
            }
        }
    }
}