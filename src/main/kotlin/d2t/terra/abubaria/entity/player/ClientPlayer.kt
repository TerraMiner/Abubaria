package d2t.terra.abubaria.entity.player


import CollisionHandler.checkCollision
import CollisionHandler.checkIfStuck
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.GamePanel.tileSizeF
import d2t.terra.abubaria.GamePanel.world
import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.inventory.Inventory
import d2t.terra.abubaria.io.devices.KeyHandler
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.io.graphics.loadImage


object ClientPlayer : Entity() {

    var inventory = Inventory(10, 5)

    val centerPos get() = location.transfer(width/2,height/2)

    fun initialize() {
        setDefaultValues()
        getPlayerImage()
    }

    private fun setDefaultValues() {

        maxXspeed = 0.8F

        hitBox.height = height * tileSizeF
        hitBox.width = width * tileSizeF

        location.x = world.worldWidth / 2F
        location.y = tileSize * 10F

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

        applyFriction()

        val prevHitBox = hitBox.clone

        checkCollision()

        if (checkIfStuck(prevHitBox)) {
            dx = .0F
            dy = .0F
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
            val mod = (System.currentTimeMillis() - jumpStart) / 17F

            if (dy < 0 && onJump || onGround || onWorldBorder) {
                dy -= (jumpHeight / if (mod < 1F) 1F else mod)
            } else if (onJump) {
                onJump = false
                KeyHandler.timeSpaceReleased = System.currentTimeMillis()
            }
        }
    }
}
