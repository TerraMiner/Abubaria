package d2t.terra.abubaria.entity.player


import CollisionHandler.checkCollision
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.GamePanel.world
import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


class Player : Entity(Location()) {


    fun initialize() {
        setDefaultValues()
        getPlayerImage()
    }

    private fun setDefaultValues() {

        hitBox.height = height * tileSize.toDouble()
        hitBox.width = width * tileSize.toDouble()

        location.x = world.worldWidth / 2.0
        location.y = 0 + tileSize*10.0

        location.direction = Direction.LEFT
    }

    private fun BufferedImage.negative(): BufferedImage {
        val negativeImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = Color(getRGB(x, y), true)
                val negativeColor = Color(255 - color.red, 255 - color.green, 255 - color.blue, color.alpha)
                negativeImage.setRGB(x, y, negativeColor.rgb)
            }
        }
        return negativeImage
    }

    private fun getPlayerImage() {
        kotlin.runCatching {
            val path = "res/entity/player/"
            val width = (tileSize * width + 4).toInt()
            val height = (tileSize * height + 1).toInt()

            leftIdle = UtilityTool.scaleImage(ImageIO.read(File("${path}leftIdle.png"))/*.negative()*/, width, height)
            leftJump = UtilityTool.scaleImage(ImageIO.read(File("${path}leftJump.png"))/*.negative()*/, width, height)
            rightIdle = UtilityTool.scaleImage(ImageIO.read(File("${path}rightIdle.png"))/*.negative()*/, width, height)
            rightJump = UtilityTool.scaleImage(ImageIO.read(File("${path}rightJump.png"))/*.negative()*/, width, height)

        }.getOrElse {
            it.printStackTrace()
            println("error while loading images")
        }
    }


    fun update() {

//        val a = LagDebugger()

        if (KeyHandler.leftPressed) {
            location.direction = Direction.LEFT
        }

        if (KeyHandler.rightPressed) {
            location.direction = Direction.RIGHT
        }

        autoClimb = !KeyHandler.downPressed
//        a.check("80")
        hitBox.keepInBounds(world.worldBorder)
//        a.check("82")
        chunks = hitBox.intersectionChunks()
//        a.check("84")
        jump()
//        a.check("86")
        checkIfOnGround()
//        a.check("88")
        fall()
//        a.check("90")
        applyMovement()
//        a.check("92")
        checkCollision()
//        a.check("94")
        location.x += dx
//        a.check("96")
        hitBox.setLocation(location)
//        a.check("98")
//        a.debug("Player Updating")

    }


    private fun jump() {
        if (!onJump && (onGround || onWorldBorder)) jumpStart = KeyHandler.timeSpacePressed
        jumpEnd = KeyHandler.timeSpaceReleased

        onJump = System.currentTimeMillis() in jumpStart..jumpEnd

        if (onJump && KeyHandler.spacePressed) {
            val mod = (System.currentTimeMillis() - jumpStart) / 17.0

            if (dy < 0 && onJump || onGround || onWorldBorder) dy -= (jumpHeight / if (mod < 1.0) 1.0 else mod)
            else if (onJump) {
                onJump = false
                KeyHandler.timeSpaceReleased = System.currentTimeMillis()
            }
        }
    }
}
