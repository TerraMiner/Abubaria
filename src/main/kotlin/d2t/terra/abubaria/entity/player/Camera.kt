package d2t.terra.abubaria.entity.player

import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.screenHeight2
import d2t.terra.abubaria.GamePanel.screenWidth2
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.GamePanel.world
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage

object Camera {
    var screenX = 0
    var screenY = 0

    var cameraDx = .0
    var cameraDy = .0

    private var interpolationFactor = 0.2

    private var targetX = 0.0
    private var targetY = 0.0

    private var centerX = (screenWidth2 / 2 - (tileSize / 2))
    private var centerY = (screenHeight2 / 2 - (tileSize / 2))

    fun interpolate() {
        screenX = centerX /*- cameraDx.toInt()*/
        screenY = centerY /*- cameraDy.toInt()*/

//        val distX = centerX - screenX
//        val distY = centerY - screenY
//
//        if (screenX != centerX) cameraDx -= distX * 0.05
//        if (screenY != centerY) cameraDy -= distY * 0.05
    }


    fun initialize() {

        targetX = ClientPlayer.location.x
        targetY = ClientPlayer.location.y
        centerX = (screenWidth2 / 2 - (tileSize / 2))
        centerY = (screenHeight2 / 2 - (tileSize / 2))
    }

    fun offsetX(location: Location) = location.x + screenX

    fun offsetY(location: Location) = location.y + screenY

    fun onsetX(location: Location) = location.x - screenX

    fun onsetY(location: Location) = location.y - screenY

    fun worldScreenPosX(defaultX: Int, location: Location): Int {
        var offX = defaultX - onsetX(location).toInt()
        val world = GamePanel.world

        if (screenX > location.x)
            offX = defaultX
        if (screenWidth2 - screenX > world.worldWidth - location.x)
            offX = screenWidth2 - (world.worldWidth - defaultX)
        return offX
    }

    fun worldScreenPosY(defaultY: Int, location: Location): Int {

        var offY = defaultY - onsetY(location).toInt()
        val world = GamePanel.world

        if (screenY > location.y)
            offY = defaultY
        if (screenHeight2 - screenY > world.worldHeight - location.y)
            offY = screenHeight2 - (world.worldHeight - defaultY)

        return offY
    }

    fun playerScreenPosX(location: Location): Int {
        var offX = screenX

        if (screenX > location.x) offX = location.x.toInt()

        if (screenWidth2 - screenX > world.worldWidth - location.x) offX =
            (screenWidth2 - (world.worldWidth - location.x)).toInt()

        return offX
    }

    fun playerScreenPosY(location: Location): Int {
        var offY = screenY

        if (screenY > location.y) offY = location.y.toInt()

        if (screenHeight2 - screenY > world.worldHeight - location.y) offY =
            (screenHeight2 - (world.worldHeight - location.y)).toInt()

        return offY
    }

    fun draw(g2: Graphics2D, location: Location) {
        ClientPlayer.apply {
            val image: BufferedImage? = when (this.location.direction) {

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

            g2.drawImage(
                image,
                offX - 1,
                offY,
                null
            )

            if (Client.debugMode) ClientPlayer.hitBox.apply {
                val prevColor = g2.color
                g2.color = Color.BLACK
                g2.drawRect(offX, offY, width.toInt(), height.toInt())
                g2.color = prevColor
            }
        }
    }
}