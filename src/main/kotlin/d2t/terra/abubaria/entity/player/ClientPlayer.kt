package d2t.terra.abubaria.entity.player


import CollisionHandler.checkCollision
import CollisionHandler.checkIfStuck
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.GamePanel.world
import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.inventory.Inventory
import d2t.terra.abubaria.io.devices.KeyHandler
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.lwjgl.loadImage
import org.lwjgl.glfw.GLFW


object ClientPlayer : Entity() {

    var inventory = Inventory(10, 5)

    fun initialize() {
        setDefaultValues()
        getPlayerImage()
    }

    private fun setDefaultValues() {

        maxXspeed = 0.8

        hitBox.height = height * tileSize.toDouble()
        hitBox.width = width * tileSize.toDouble()

        location.x = world.worldWidth / 2.0
        location.y = 0 + tileSize * 10.0

        location.direction = Direction.LEFT
    }

    private fun getPlayerImage() {
        val path = "entity/player/"

        leftIdle = loadImage("${path}leftIdle.png")
        leftJump = loadImage("${path}leftJump.png")
        rightIdle = loadImage("${path}rightIdle.png")
        rightJump = loadImage("${path}rightJump.png")
    }


    override fun update() {

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

        val prevHitBox = hitBox.clone

        checkCollision()

        if (checkIfStuck(prevHitBox)) {
            dx = .0
            dy = .0
        }

        location.x += dx
        location.y += dy

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
