package d2t.terra.abubaria.light

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.GamePanel.tileSizeF
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.io.graphics.drawFillRect
import d2t.terra.abubaria.io.graphics.safetyDraw
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.window
import d2t.terra.abubaria.world.block.Block
import d2t.terra.abubaria.world.chunkSize
import d2t.terra.abubaria.world.lSize
import d2t.terra.abubaria.world.lSizeF
import d2t.terra.abubaria.world.material.Material
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.GL_QUADS
import java.util.concurrent.ConcurrentLinkedQueue

object LightManager {

    val forUpDate = ConcurrentLinkedQueue<Block>()

    var rectsToDraw = listOf<LightRect>()

    fun calculateToDraw(location: Location) {
        val tileChunkSize = tileSizeF * chunkSize
        rectsToDraw = Camera.chunksOnScreen.flatMap { chunk ->
            when {
                !chunk.lighed -> listOf(LightRect(
                    Camera.worldScreenPosX(chunk.x * tileChunkSize.toInt(), location),
                    Camera.worldScreenPosY(chunk.y * tileChunkSize.toInt(), location),
                    tileChunkSize, tileChunkSize, 255
                ))
                else -> chunk.blockMap.flatMapIndexed { x, col ->
                    val screenX = Camera.worldScreenPosX((chunk.x * chunkSize + x) * tileSize, location)
                    col.flatMapIndexed { y, block ->
                        if (block.type === Material.AIR) emptyList() else {
                            val screenY = Camera.worldScreenPosY(
                                (chunk.y * chunkSize + y) * tileSize, location
                            ) + (tileSize * block.type.state.offset).toInt()
                            if (block.lighted)
                                block.lightMap.flatten().map { light -> LightRect(
                                    screenX + light.inBlockX * lSize, screenY + light.inBlockY * lSize,
                                    lSizeF, lSizeF, light.power * 16
                                )
                                } else listOf(LightRect(screenX, screenY, tileSizeF, block.type.height, 255))
                        }
                    }
                }
            }
        }
    }

    fun draw() {
        safetyDraw(GL_QUADS) {
            rectsToDraw.forEach { rect ->
                drawFillRect(rect.x, rect.y, rect.width, rect.height, rect.power)
            }
        }
    }

    fun tick() {

        var lastTime = System.nanoTime()
        var currentTime: Long
        var timer = 0L
        var tickCount = 0
        while (!GLFW.glfwWindowShouldClose(window)) {
            currentTime = System.nanoTime()
            timer += (currentTime - lastTime)
            lastTime = currentTime

            forUpDate.toList().forEach { block ->
                block.lightMap.flatten().forEach {
                    it.initializePower()
                }
                forUpDate.remove(block)
            }

            ++tickCount

            if (timer >= 1000000000) {
                GamePanel.display.lps = tickCount
                tickCount = 0
                timer = 0
            }
        }
    }
}