package d2t.terra.abubaria.entity.player

import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.player
import d2t.terra.abubaria.GamePanel.screenHeight2
import d2t.terra.abubaria.GamePanel.screenWidth2
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.GamePanel.world
import d2t.terra.abubaria.location.Direction
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
        screenX = centerX - cameraDx.toInt()
        screenY = centerY - cameraDy.toInt()

        val distX = centerX - screenX
        val distY = centerY - screenY

        if (screenX != centerX) cameraDx -= distX*0.001
        if (screenY != centerY) cameraDy -= distY*0.001
    }


    fun initialize() {

        targetX = player.location.x.toDouble()
        targetY = player.location.y.toDouble()
        centerX = (screenWidth2 / 2 - (tileSize / 2))
        centerY = (screenHeight2 / 2 - (tileSize / 2))
    }

    fun offsetX(player: Player) = player.location.x + screenX

    fun offsetY(player: Player) = player.location.y + screenY

    fun onsetX(player: Player) = player.location.x - screenX

    fun onsetY(player: Player) = player.location.y - screenY

    fun worldScreenPosX(defaultX: Int): Int {
        var offX = defaultX - onsetX(player).toInt()
        val location = player.location
        val world = GamePanel.world

        if (screenX > location.x)
            offX = defaultX
        if (screenWidth2 - screenX > world.worldWidth - location.x)
            offX = screenWidth2 - (world.worldWidth - defaultX)
        return offX
    }

    fun worldScreenPosY(defaultY: Int): Int {
        var offY = defaultY - onsetY(player).toInt()
        val location = player.location
        val world = GamePanel.world

        if (screenY > location.y)
            offY = defaultY
        if (screenHeight2 - screenY > world.worldHeight - location.y)
            offY = screenHeight2 - (world.worldHeight - defaultY)

        return offY
    }

    fun playerScreenPosX(): Int {
        var offX = screenX
        val location = player.location

        if (screenX > player.location.x) offX = player.location.x.toInt()

        if (screenWidth2 - screenX > world.worldWidth - location.x) offX =
            (screenWidth2 - (world.worldWidth - location.x)).toInt()

        return offX
    }

    fun playerScreenPosY(): Int {
        var offY = screenY
        val location = player.location

        if (screenY > player.location.y) offY = player.location.y.toInt()

        if (screenHeight2 - screenY > world.worldHeight - location.y) offY =
            (screenHeight2 - (world.worldHeight - location.y)).toInt()

        return offY
    }

    fun draw(g2: Graphics2D) {
        player.apply {
            val image: BufferedImage? = when (player.location.direction) {

                Direction.LEFT -> {
                    if (onGround || onWorldBorder) leftIdle
                    else leftJump
                }

                Direction.RIGHT -> {
                    if (onGround || onWorldBorder) rightIdle
                    else rightJump
                }
            }

            val offX = playerScreenPosX()
            val offY = playerScreenPosY()

            g2.drawImage(
                image,
                offX - 1,
                offY,
                null
            )

            if (Client.debugMode) player.hitBox.apply {
                val prevColor = g2.color
                g2.color = color
                g2.drawRect(offX, offY, width.toInt(), height.toInt())
                g2.color = prevColor
            }
        }
    }
}