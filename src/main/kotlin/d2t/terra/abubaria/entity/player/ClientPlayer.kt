package d2t.terra.abubaria.entity.player


import CollisionHandler.checkCollision
import KeyHandler
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.GamePanel.world
import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location
import scaleImage
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


object ClientPlayer : Entity(Location()) {

    init {
        setDefaultValues()
        getPlayerImage()
    }


    private fun setDefaultValues() {

        hitBox.height = height * tileSize.toDouble()
        hitBox.width = width * tileSize.toDouble()

        location.x = world.worldWidth / 2.0
        location.y = 0 + tileSize * 10.0

        location.direction = Direction.LEFT
    }

    private fun getPlayerImage() {
        kotlin.runCatching {
            val path = "res/entity/player/"
            val width = (tileSize * width + 4).toInt()
            val height = (tileSize * height + 1).toInt()

            leftIdle = scaleImage(ImageIO.read(File("${path}leftIdle.png"))/*.negative()*/, width, height)
            leftJump = scaleImage(ImageIO.read(File("${path}leftJump.png"))/*.negative()*/, width, height)
            rightIdle = scaleImage(ImageIO.read(File("${path}rightIdle.png"))/*.negative()*/, width, height)
            rightJump = scaleImage(ImageIO.read(File("${path}rightJump.png"))/*.negative()*/, width, height)

        }.getOrElse {
            it.printStackTrace()
            println("error while loading images")
        }
    }


    fun update() {

        if (KeyHandler.leftPressed) {
            location.direction = Direction.LEFT
        }

        if (KeyHandler.rightPressed) {
            location.direction = Direction.RIGHT
        }

        autoClimb = !KeyHandler.downPressed

        hitBox.keepInBounds(world.worldBorder)

        chunks = hitBox.intersectionChunks()

        jump()

        checkIfOnGround()

        fall()

        applyMovement()

        checkCollision()

        location.x += dx

        hitBox.setLocation(location)
    }


    private fun jump() {
        if (!onJump && (onGround || onWorldBorder)) jumpStart = KeyHandler.timeSpacePressed
        jumpEnd = KeyHandler.timeSpaceReleased

        onJump = System.currentTimeMillis() in jumpStart..jumpEnd

        if (onJump && KeyHandler.spacePressed) {
            val mod = (System.currentTimeMillis() - jumpStart) / 17.0

            if (dy < 0 && onJump || onGround || onWorldBorder) {
                dy -= (jumpHeight / if (mod < 1.0) 1.0 else mod)
            } else if (onJump) {
                onJump = false
                KeyHandler.timeSpaceReleased = System.currentTimeMillis()
            }
        }
    }
}
