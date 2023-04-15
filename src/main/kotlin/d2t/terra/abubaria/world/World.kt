package d2t.terra.abubaria.world

import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.location.BlockHitBox
import d2t.terra.abubaria.location.HitBox
import d2t.terra.abubaria.world.tile.Material
import d2t.terra.abubaria.world.tile.Material.*
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import kotlin.math.abs
import kotlin.random.Random


class Block(var material: Material = AIR, var x: Int = 0, var y: Int = 0, var chunkX: Int = 0, var chunkY: Int = 0) {
    var hitBox = BlockHitBox(this)
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

    val worldSizeX = 32
    val worldSizeY = 32

    val worldWidth = tileSize * chunkSize * worldSizeX
    val worldHeight = tileSize * chunkSize * worldSizeY

    val worldBorder = HitBox(0, 0, worldWidth, worldHeight)

    val chunks = Array(worldSizeX) { Array(worldSizeY) { Chunk() } }

    fun getChunkAt(x: Int, y: Int): Chunk? {
        return chunks.getOrNull(x / chunkSize)
            ?.getOrNull(y / chunkSize)
    }

    fun getBlockAt(x: Int, y: Int): Block? {
        val chunk = getChunkAt(x, y) ?: return null

        return chunk.blocks.getOrNull(x - chunk.x * chunkSize)?.getOrNull(y - chunk.y * chunkSize)
    }

    fun setBlock(material: Material, x: Int, y: Int) {
        val chunk = getChunkAt(x, y) ?: return
        chunk.blocks[x - chunk.x * chunkSize][y - chunk.y * chunkSize].material = material
    }


    fun generate() {
        for (x in 0 until worldSizeX) {
            for (y in 0 until worldSizeY) {
                val chunk = Chunk(x, y).apply { initBlocks() }

                val blocks = chunk.blocks.flatten()

                val halfChunkHeight = worldSizeY / 2
                kotlin.runCatching {
                    when {

                        y == halfChunkHeight -> {
                            blocks.forEach { it.material = DIRT }
                        }

                        y + 1 == halfChunkHeight -> {
                            blocks.forEach { it.material = DIRT }
                        }

                        y + 2 == halfChunkHeight -> {
                            blocks.forEach { it.material = DIRT }
                        }

                        y + 2 > halfChunkHeight -> {
                            blocks.forEach { it.material = STONE }
                        }
                    }
                }



                chunks[x][y] = chunk

                /*if (y > worldSizeY / 2) setBlock(Material.values().drop(1).random(), x, y)
                else *//*setBlock(AIR, x, y)*/
            }
        }

        chunks.flatten().map { it.blocks.flatten() }.flatten().forEach {
            if (it.material === DIRT && getBlockAt(it.x,it.y-1)?.material === AIR) it.material = GRASS
        }
    }

    fun draw(g2: Graphics2D) {
        val player = GamePanel.player

        val offsetX = Camera.offsetX(player)
        val offsetY = Camera.offsetY(player)
        val onsetX = Camera.onsetX(player)
        val onsetY = Camera.onsetY(player)

        val extraDrawDistX = abs((Camera.playerScreenPosX() - Camera.screenX) / tileSize / chunkSize) + 2
        val extraDrawDistY = abs((Camera.playerScreenPosY() - Camera.screenY) / tileSize / chunkSize) + 2

        g2.color = Color.BLACK

        chunks.forEachIndexed { chunkX, chunkCols ->

            val cwx = chunkX * chunkSize * tileSize

            chunkCols.forEachIndexed { chunkY, chunk ->

                val cwy = chunkY * chunkSize * tileSize

                if (cwx + extraDrawDistX * chunkSize * tileSize > onsetX &&
                    cwx - extraDrawDistX * chunkSize * tileSize < offsetX &&
                    cwy + extraDrawDistY * chunkSize * tileSize > onsetY &&
                    cwy - extraDrawDistY * chunkSize * tileSize < offsetY
                ) {
                    chunk.draw(g2)
                }
            }
        }
    }

    private fun Chunk.draw(g2: Graphics2D) {
        blocks.forEachIndexed { x, blockCols ->
            val worldX = (this.x * chunkSize + x) * tileSize
            blockCols.forEachIndexed { y, block ->
                val worldY = (this.y * chunkSize + y) * tileSize
                block.draw(worldX, worldY, g2)
            }
        }

        if (Client.debugMode) {
            val prevColor = g2.color
            g2.color = hitBox.color

            val screenX = Camera.worldScreenPosX(x * tileSize * chunkSize)
            val screenY = Camera.worldScreenPosY(y * tileSize * chunkSize)

            g2.drawRect(screenX, screenY, hitBox.width.toInt(), hitBox.height.toInt())

            g2.font = Font("TimesRoman", Font.PLAIN, 15)
            g2.drawString("x: $x, y: $y", screenX, screenY + tileSize / 2)
            g2.color = prevColor
        }
    }


    private fun Block.draw(worldX: Int, worldY: Int, g2: Graphics2D) {

        val screenX = Camera.worldScreenPosX(worldX) /*- onsetX*/
        val screenY = Camera.worldScreenPosY(worldY) /*- onsetY*/

        g2.drawImage(material.texture, screenX, screenY + material.state.offset, null)

        if (this != GamePanel.cursor.currentBlock) hitBox.color = null

        if (Client.debugMode) hitBox.apply {
            val prevColor = g2.color
            g2.color = color ?: return@apply

            g2.drawRect(screenX, screenY + block.material.state.offset, this.width.toInt(), this.height.toInt())

            g2.drawString("${block.x} ${block.y}", screenX, screenY + block.material.state.offset)

            g2.color = prevColor
        }
    }
}