package d2t.terra.abubaria.entity.player

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.screenHeight
import d2t.terra.abubaria.GamePanel.screenWidth
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.GamePanel.world
import d2t.terra.abubaria.io.graphics.drawTexture
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.Chunk

object Camera {
    var cameraX = 0f
    var cameraY = 0f

    var centerX = (screenWidth / 2.0 - (tileSize / 2)).toFloat()
    var centerY = (screenHeight / 2.0 - (tileSize / 2)).toFloat()

    fun interpolate() {
        cameraX = centerX
        cameraY = centerY
    }


    fun initialize() {

        centerX = (screenWidth / 2.0 - (tileSize / 2)).toFloat()
        centerY = (screenHeight / 2.0 - (tileSize / 2)).toFloat()

    }

    fun rightCameraX(targetLocation: Location) = targetLocation.x + cameraX

    fun bottomCameraY(targetLocation: Location) = targetLocation.y + cameraY

    fun leftCameraX(targetLocation: Location) = targetLocation.x - cameraX

    fun topCameraY(targetLocation: Location) = targetLocation.y - cameraY

    fun worldScreenPosX(defaultX: Int, targetLocation: Location): Float {
        var offX = defaultX - leftCameraX(targetLocation)
        val world = GamePanel.world

        if (cameraX > targetLocation.x)
            offX = defaultX.toFloat()
        if (screenWidth - cameraX > world.worldWidth - targetLocation.x)
            offX = (screenWidth - (world.worldWidth - defaultX)).toFloat()

        return offX
    }

    fun worldScreenPosY(defaultY: Int, targetLocation: Location): Float {

        var offY = defaultY - topCameraY(targetLocation)
        val world = GamePanel.world

        if (cameraY > targetLocation.y)
            offY = defaultY.toFloat()
        if (screenHeight - cameraY > world.worldHeight - targetLocation.y)
            offY = (screenHeight - (world.worldHeight - defaultY)).toFloat()

        return offY
    }

    fun playerScreenPosX(targetLocation: Location): Float {
        var offX = cameraX

        if (cameraX > targetLocation.x) offX = targetLocation.x

        if (screenWidth - cameraX > world.worldWidth - targetLocation.x) offX =
            (screenWidth - (world.worldWidth - targetLocation.x))

        return offX
    }

    fun playerScreenPosY(targetLocation: Location): Float {
        var offY = cameraY

        if (cameraY > targetLocation.y) offY = targetLocation.y

        if (screenHeight - cameraY > world.worldHeight - targetLocation.y) offY =
            (screenHeight - (world.worldHeight - targetLocation.y))

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
            }

            val offX = playerScreenPosX(location)
            val offY = playerScreenPosY(location)

            val width = (tileSize * width + 4)
            val height = (tileSize * height + 1)

            drawTexture(image?.textureId, offX - 1, offY, width, height)

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