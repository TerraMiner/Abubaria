package d2t.terra.abubaria.entity.player

import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.GamePanel.tileSizeF
import d2t.terra.abubaria.GamePanel.world
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.io.graphics.render.RendererManager
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.chunkSize
import d2t.terra.abubaria.io.graphics.Model

object Camera {
    var cameraX = 0f
    var cameraY = 0f

    fun interpolate(location: Location) {
        val leftCorner = 0f
        val rightCorner = -world.worldChunkWidth * chunkSize * tileSizeF + Window.width
        cameraX = -location.x + Window.centerX.toFloat()
        if (cameraX > leftCorner) cameraX = leftCorner
        if (cameraX < rightCorner) cameraX = rightCorner

        val upCorner = 0f
        val bottomCorner = -world.worldChunkHeight * chunkSize * tileSizeF + Window.height
        cameraY = -location.y + Window.centerY.toFloat()
        if (cameraY > upCorner) cameraY = upCorner
        if (cameraY < bottomCorner) cameraY = bottomCorner
    }

    fun playerScreenPosX(targetLocation: Location): Float {
        var offX = if (cameraX == 0f) targetLocation.x
        else if (cameraX == -world.worldChunkWidth * chunkSize * tileSizeF + Window.width) cameraX + targetLocation.x
        else Window.centerX.toFloat()
        return offX
    }

    fun playerScreenPosY(targetLocation: Location): Float {
        var offX = if (cameraY == 0f) targetLocation.y
        else if (cameraY == -world.worldChunkWidth * chunkSize * tileSizeF + Window.height) cameraY + targetLocation.y
        else Window.centerY.toFloat()
        return offX
    }

    fun rightCameraX(targetLocation: Location) = targetLocation.x + cameraX

    fun bottomCameraY(targetLocation: Location) = targetLocation.y + cameraY

    fun leftCameraX(targetLocation: Location) = targetLocation.x - cameraX

    fun topCameraY(targetLocation: Location) = targetLocation.y - cameraY

    fun worldScreenPosX(defaultX: Int, targetLocation: Location): Float {
        var offX = defaultX - leftCameraX(targetLocation)
        val world = world

        if (cameraX > targetLocation.x)
            offX = defaultX.toFloat()
        if (Window.width - cameraX > world.width - targetLocation.x)
            offX = (Window.width - (world.width - defaultX)).toFloat()

        return offX
    }

    fun worldScreenPosY(defaultY: Int, targetLocation: Location): Float {

        var offY = defaultY - topCameraY(targetLocation)
        val world = world

        if (cameraY > targetLocation.y)
            offY = defaultY.toFloat()
        if (Window.height - cameraY > world.height - targetLocation.y)
            offY = (Window.height - (world.height - defaultY)).toFloat()

        return offY
    }

    fun draw(location: Location) {
        ClientPlayer.apply {
            val image = when (this.location.direction) {

                Direction.LEFT -> {
                    if (onGround || onWorldBorder) leftIdle
                    else leftJump
                }

                Direction.RIGHT -> {
                    if (onGround || onWorldBorder) rightIdle
                    else rightJump
                }
            } ?: return

            val offX = playerScreenPosX(location)
            val offY = playerScreenPosY(location)

            val width = width * tileSizeF
            val height = height * tileSizeF
            RendererManager.UIRenderer.render(image, Model.DEFAULT, offX, offY, width, height)

//            if (Client.debugMode) {
//                safetyRects {
//                    ClientPlayer.hitBox.apply {
//                        drawRect(offX, offY, this.width.toInt(), this.height.toInt())
//                    }
//                }
//            }
        }
    }
}