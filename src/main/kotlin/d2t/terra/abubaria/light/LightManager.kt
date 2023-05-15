package d2t.terra.abubaria.light

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.io.graphics.drawFillRect
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.chunkSize
import d2t.terra.abubaria.world.lSize
import d2t.terra.abubaria.world.material.Material

object LightManager {
    val lightRects = mutableListOf<LightRect>()

    fun flattenLight() {
        Camera.chunksOnScreen.map { it.blockMap.flatten() }.flatten().map { it.lightMap.flatten() }
    }

    fun draw(location: Location) {
        Camera.chunksOnScreen.forEach {
            it.blockMap.forEachIndexed x@{ x, blockCols ->
                val worldX = (it.x * chunkSize + x) * GamePanel.tileSize
                blockCols.forEachIndexed y@{ y, block ->
                    val worldY = (it.y * chunkSize + y) * GamePanel.tileSize

                    if (block.type === Material.AIR) return@y

                    val screenX = Camera.worldScreenPosX(worldX, location)
                    val screenY = (Camera.worldScreenPosY(worldY, location) + (GamePanel.tileSize * block.type.state.offset).toInt())

                    block.lightMap.flatten().forEach { light ->
                        drawFillRect(
                            screenX + light.inBlockX * lSize,
                            screenY + light.inBlockY * lSize, lSize, lSize, light.power * 16
                        )
                    }
                }
            }
        }
    }


//    lightMap.flatten().forEach { light ->
//        if (type != Material.AIR) {
//            drawFillRect(
//                screenX + light.inBlockX * lSize,
//                screenY + light.inBlockY * lSize, lSize, lSize, light.power * 16)
//        }
//    }
}