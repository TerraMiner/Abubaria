package d2t.terra.abubaria.entity.player

import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.screenHeight
import d2t.terra.abubaria.GamePanel.screenWidth
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.GamePanel.world
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.io.graphics.drawRect
import d2t.terra.abubaria.io.graphics.drawTexture
import d2t.terra.abubaria.world.Chunk

object Camera {
    var cameraX = 0
    var cameraY = 0

    var centerX = (screenWidth / 2.0 - (tileSize / 2)).toInt()
    var centerY = (screenHeight / 2.0 - (tileSize / 2)).toInt()

    var chunksOnScreen = mutableListOf<Chunk>()

    fun interpolate() {
        cameraX = centerX
        cameraY = centerY
    }


    fun initialize() {

        centerX = (screenWidth / 2.0 - (tileSize / 2)).toInt()
        centerY = (screenHeight / 2.0 - (tileSize / 2)).toInt()

    }

    fun rightCameraX(targetLocation: Location) = targetLocation.x + cameraX

    fun bottomCameraY(targetLocation: Location) = targetLocation.y + cameraY

    fun leftCameraX(targetLocation: Location) = targetLocation.x - cameraX

    fun topCameraY(targetLocation: Location) = targetLocation.y - cameraY

    fun worldScreenPosX(defaultX: Int, targetLocation: Location): Int {
        var offX = defaultX - leftCameraX(targetLocation)
        val world = GamePanel.world

        if (cameraX > targetLocation.x)
            offX = defaultX.toDouble()
        if (screenWidth - cameraX > world.worldWidth - targetLocation.x)
            offX = (screenWidth - (world.worldWidth - defaultX)).toDouble()

        return offX.toInt()
    }

    fun worldScreenPosY(defaultY: Int, targetLocation: Location): Int {

        var offY = defaultY - topCameraY(targetLocation)
        val world = GamePanel.world

        if (cameraY > targetLocation.y)
            offY = defaultY.toDouble()
        if (screenHeight - cameraY > world.worldHeight - targetLocation.y)
            offY = (screenHeight - (world.worldHeight - defaultY)).toDouble()

        return offY.toInt()
    }

    fun playerScreenPosX(targetLocation: Location): Int {
        var offX = cameraX

        if (cameraX > targetLocation.x) offX = targetLocation.x.toInt()

        if (screenWidth - cameraX > world.worldWidth - targetLocation.x) offX =
            (screenWidth - (world.worldWidth - targetLocation.x)).toInt()

        return offX
    }

    fun playerScreenPosY(targetLocation: Location): Int {
        var offY = cameraY

        if (cameraY > targetLocation.y) offY = targetLocation.y.toInt()

        if (screenHeight - cameraY > world.worldHeight - targetLocation.y) offY =
            (screenHeight - (world.worldHeight - targetLocation.y)).toInt()

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

            val width = (tileSize * width + 4).toInt()
            val height = (tileSize * height + 1).toInt()

            drawTexture(image?.textureId, offX - 1, offY, width, height)

            if (Client.debugMode) ClientPlayer.hitBox.apply {
                drawRect(offX, offY, this.width.toInt(), this.height.toInt())
            }
        }
    }
}