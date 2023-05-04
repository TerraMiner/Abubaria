package d2t.terra.abubaria.entity.player

import d2t.terra.abubaria.Client
import d2t.terra.abubaria.Client.currentZoom
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.screenHeight
import d2t.terra.abubaria.GamePanel.screenWidth
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.GamePanel.world
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.HitBox
import d2t.terra.abubaria.location.Location
import lwjgl.drawRect
import lwjgl.drawTexture

object Camera {
    var screenX = 0
    var screenY = 0

    private var targetX = 0.0
    private var targetY = 0.0

    var centerX = (screenWidth / 2 - (tileSize / 2))
    var centerY = (screenHeight / 2 - (tileSize / 2))

    val box = HitBox(0, 0, screenWidth, screenHeight)
    val boxResolution get() = box.width / box.height
    val screenResolution get() = screenWidth.toDouble() / screenHeight
    val boxScreenDivX get() = box.width / screenWidth
    val boxScreenDivY get() = box.height / screenHeight

    fun interpolate() {
//        screenX = centerX
//        screenY = centerY
        screenX = (box.width / 2 - (tileSize / 2)).toInt()
        screenY = (box.height / 2 - (tileSize / 2)).toInt()
    }


    fun initialize() {

        targetX = ClientPlayer.location.x
        targetY = ClientPlayer.location.y
        centerX = (screenWidth / 2 - (tileSize / 2))
        centerY = (screenHeight / 2 - (tileSize / 2))

        box.apply {
            width = screenWidth - currentZoom * 2.0
            height = screenHeight - currentZoom * 2.0
        }

    }

    fun offsetX(location: Location) = location.x + screenX

    fun offsetY(location: Location) = location.y + screenY

    fun onsetX(location: Location) = location.x - screenX

    fun onsetY(location: Location) = location.y - screenY

    fun worldScreenPosX(defaultX: Int, location: Location): Int {
        var offX = defaultX - onsetX(location).toInt()
        val world = GamePanel.world

//        if (screenX > location.x)
//            offX = defaultX
//        if (screenWidth - screenX > world.worldWidth - location.x)
//            offX = screenWidth - (world.worldWidth - defaultX)
//
        if (screenX > location.x)
            offX = defaultX
        if (box.width - screenX > world.worldWidth - location.x)
            offX = (box.width - (world.worldWidth - defaultX)).toInt()
//
//        println("------------------")
//        println(boxResolution)
//        println(screenResolution)
        return (offX / boxScreenDivX).toInt()
    }

    fun worldScreenPosY(defaultY: Int, location: Location): Int {

        var offY = defaultY - onsetY(location).toInt()
        val world = GamePanel.world

//        if (screenY > location.y)
//            offY = defaultY
//        if (screenHeight - screenY > world.worldHeight - location.y)
//            offY = screenHeight - (world.worldHeight - defaultY)
//
        if (screenY > location.y)
            offY = defaultY
        if (box.height - screenY > world.worldHeight - location.y)
            offY = (box.height - (world.worldHeight - defaultY)).toInt()
//
        return (offY / boxScreenDivY).toInt()
//        return offY
    }

    fun playerScreenPosX(location: Location): Int {
        var offX = screenX

        if (screenX > location.x) offX = location.x.toInt()

//        if (screenWidth - screenX > world.worldWidth - location.x) offX =
//            (screenWidth - (world.worldWidth - location.x)).toInt()
//
        if (box.width - screenX > world.worldWidth - location.x) offX =
            (box.width - (world.worldWidth - location.x)).toInt()
//
        return (offX / (box.width / screenWidth)).toInt()
//        return offX
    }

    fun playerScreenPosY(location: Location): Int {
        var offY = screenY

        if (screenY > location.y) offY = location.y.toInt()

//        if (screenHeight - screenY > world.worldHeight - location.y) offY =
//            (screenHeight - (world.worldHeight - location.y)).toInt()
//
        if (box.height - screenY > world.worldHeight - location.y) offY =
            (box.height - (world.worldHeight - location.y)).toInt()
//
        return (offY / (box.height / screenHeight)).toInt()
//        return offY
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

//            g2.drawImage(image, offX - 1, offY, null)

            if (Client.debugMode) ClientPlayer.hitBox.apply {
//                val prevColor = g2.color
//                g2.color = Color.BLACK
                drawRect(offX, offY, this.width.toInt(), this.height.toInt())
//                g2.color = prevColor
            }
        }
    }
}