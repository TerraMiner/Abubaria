package d2t.terra.abubaria.light

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.io.graphics.drawFillRect
import d2t.terra.abubaria.io.graphics.safetyRects
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.window
import d2t.terra.abubaria.world.block.Block
import d2t.terra.abubaria.world.chunkSize
import d2t.terra.abubaria.world.lSize
import d2t.terra.abubaria.world.material.Material
import org.lwjgl.glfw.GLFW
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentLinkedQueue
import javax.xml.bind.JAXBElement.GlobalScope

object LightManager {

    val forUpDate = ConcurrentLinkedQueue<Block>()

    var rectsToDraw = listOf<LightRect>()


    fun calculateToDraw(location: Location) {

        val tileChunkSize = tileSize * chunkSize
        val rects: List<LightRect> = Camera.chunksOnScreen.flatMap { chunk ->
            if (chunk.fullShadowed) {
                listOf(
                    LightRect(
                        Camera.worldScreenPosX(chunk.x * tileChunkSize, location),
                        Camera.worldScreenPosY(chunk.y * tileChunkSize, location),
                        tileChunkSize, tileChunkSize, 255
                    )
                )
            } else {
                chunk.blockMap.flatMapIndexed { x, col ->
                    val screenX = Camera.worldScreenPosX((chunk.x * chunkSize + x) * tileSize, location)
                    col.flatMapIndexed { y, block ->
                        if (block.type === Material.AIR) { emptyList()
                        } else {
                            val screenY = Camera.worldScreenPosY(
                                (chunk.y * chunkSize + y) * tileSize,
                                location
                            ) + (tileSize * block.type.state.offset).toInt()

                            if (!block.fullShadowed) {
                                block.lightMap.flatten().map { light ->
                                    LightRect(
                                        screenX + light.inBlockX * lSize, screenY + light.inBlockY * lSize,
                                        lSize, lSize, light.power * 16
                                    )
                                }
                            } else {
                                listOf(LightRect(screenX, screenY, tileSize, block.type.height, 255))
                            }
                        }
                    }
                }
            }
        }

        rectsToDraw = rects
    }

    fun draw() {
        safetyRects {
            rectsToDraw.forEach { rect ->
                drawFillRect(rect.x, rect.y, rect.width, rect.height, rect.power)
            }
        }
    }

    fun tick() {
        val tickInterval = 1e9 / 1000.0
        var deltaTicks = .0
        var lastTime = System.nanoTime()
        var currentTime: Long
        var timer = 0L
        var tickCount = 0
        while (!GLFW.glfwWindowShouldClose(window)) {
            currentTime = System.nanoTime()
            deltaTicks += (currentTime - lastTime) / tickInterval
            timer += (currentTime - lastTime)
            lastTime = currentTime
            while (deltaTicks >= 1.0) {
                forUpDate.toList().forEach { block ->
                    block.lightMap.flatten().forEach {
                        it.initializePower()
                    }
                    forUpDate.remove(block)
                }
                --deltaTicks
                ++tickCount
            }

            if (timer >= 1000000000) {
                GamePanel.display.lps = tickCount
                tickCount = 0
                timer = 0
            }
        }
    }
}