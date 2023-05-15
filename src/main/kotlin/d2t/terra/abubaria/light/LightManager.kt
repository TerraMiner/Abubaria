package d2t.terra.abubaria.light

import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.io.graphics.drawFillRect
import d2t.terra.abubaria.io.graphics.safetyRects
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.Chunk
import d2t.terra.abubaria.world.block.Block
import d2t.terra.abubaria.world.chunkSize
import d2t.terra.abubaria.world.lSize
import d2t.terra.abubaria.world.material.Material

object LightManager {

    fun draw(location: Location) {
        val tileChunkSize = tileSize * chunkSize
        val rectsToDraw: List<LightRect> = Camera.chunksOnScreen.flatMap { chunk ->
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
                    col.flatMapIndexed { y, block ->
                        if (block.type === Material.AIR) {
                            emptyList() // явное преобразование к типу List<Rect>
                        } else {
                            val screenX = Camera.worldScreenPosX((chunk.x * chunkSize + x) * tileSize, location)
                            val screenY = Camera.worldScreenPosY((chunk.y * chunkSize + y) * tileSize, location) + (tileSize * block.type.state.offset).toInt()

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
        safetyRects {
            rectsToDraw.forEach { rect ->
                drawFillRect(rect.x, rect.y, rect.width, rect.height, rect.power)
            }
        }
    }
}