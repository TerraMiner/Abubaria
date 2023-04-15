package d2t.terra.abubaria.world

import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel.bgColor
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.location.BlockHitBox
import d2t.terra.abubaria.location.HitBox
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.tile.Material
import d2t.terra.abubaria.world.tile.Material.*
import java.awt.Color
import java.awt.Graphics2D
import kotlin.math.abs


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

    val worldSizeX = 16
    val worldSizeY = 16

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
        getBlockAt(x,y)?.material = material
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
    }

    fun draw(g2: Graphics2D, location: Location) {

        val offsetX = Camera.offsetX(location)
        val offsetY = Camera.offsetY(location)
        val onsetX = Camera.onsetX(location)
        val onsetY = Camera.onsetY(location)

        val extraDrawDistX = abs((Camera.playerScreenPosX(location) - Camera.screenX) / tileSize / chunkSize) + 2
        val extraDrawDistY = abs((Camera.playerScreenPosY(location) - Camera.screenY) / tileSize / chunkSize) + 2

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
                    chunk.draw(g2, location)
                }
            }
        }
    }

    private fun Chunk.draw(g2: Graphics2D, location: Location) {
        blocks.forEachIndexed { x, blockCols ->
            val worldX = (this.x * chunkSize + x) * tileSize
            blockCols.forEachIndexed { y, block ->
                val worldY = (this.y * chunkSize + y) * tileSize
                block.draw(worldX, worldY, g2, location)
            }
        }

        if (Client.debugMode) {
            val prevColor = g2.color
            g2.color = Color.BLACK

            val screenX = Camera.worldScreenPosX(x * tileSize * chunkSize, location)
            val screenY = Camera.worldScreenPosY(y * tileSize * chunkSize, location)

            g2.drawRect(screenX, screenY, hitBox.width.toInt(), hitBox.height.toInt())

            g2.drawString("x: $x, y: $y", screenX + 3, screenY + 14)
            g2.color = prevColor
        }
    }


    private fun Block.draw(worldX: Int, worldY: Int, g2: Graphics2D, location: Location) {
        val screenX = Camera.worldScreenPosX(worldX, location)
        val screenY = Camera.worldScreenPosY(worldY, location)

//        val pipette = g2.color
//        g2.color = bgColor
//        if (material === AIR) g2.fillRect(screenX, screenY, tileSize, tileSize)
       /* else */g2.drawImage(material.texture, screenX, screenY + material.state.offset, null)
//        g2.color = pipette
    }
}