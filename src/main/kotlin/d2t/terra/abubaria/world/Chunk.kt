package d2t.terra.abubaria.world

import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.hitbox.BlockHitBox
import d2t.terra.abubaria.hitbox.HitBox
import d2t.terra.abubaria.io.graphics.drawRect
import d2t.terra.abubaria.io.graphics.drawString
import d2t.terra.abubaria.io.graphics.safetyDraw
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.block.Block
import org.lwjgl.opengl.GL11.GL_LINE_LOOP
import org.lwjgl.opengl.GL11.glLineWidth
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
        val worldSizeX = x * chunkSize
        val worldSizeY = y * chunkSize
        blockMap.forEachIndexed { x, blockCols ->
            val screenX = Camera.worldScreenPosX((worldSizeX + x) * tileSize, location)

            blockCols.forEachIndexed yEachIndexed@{ y, block ->
                if (block.fullShadowed) return@yEachIndexed

                val screenY = (Camera.worldScreenPosY(
                    (worldSizeY + y) * tileSize,
                    location
                ) + (tileSize * block.type.state.offset).toInt())

                block.draw(screenX, screenY)
            }
        }

        if (Client.debugMode) {
            val screenX = Camera.worldScreenPosX(worldSizeX * tileSize, location)
            val screenY = Camera.worldScreenPosY(worldSizeY * tileSize, location)
            drawString("x: $x, y: $y", screenX + 3, screenY + 14, 4, Color.BLACK)

            safetyDraw(GL_LINE_LOOP) {
                glLineWidth(1f)
                drawRect(screenX, screenY, hitBox.width, hitBox.height)
            }
        }
    }


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
