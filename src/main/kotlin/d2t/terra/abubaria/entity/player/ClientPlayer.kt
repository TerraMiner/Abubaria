package d2t.terra.abubaria.entity.player


import CollisionHandler.checkCollision
import CollisionHandler.checkIfStuck
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.GamePanel.tileSizeF
import d2t.terra.abubaria.GamePanel.world
import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.inventory.Inventory
import d2t.terra.abubaria.io.devices.KeyHandler
import d2t.terra.abubaria.io.devices.Keys
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.io.graphics.Texture


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

        location.setLocation(world.spawnLocation)
    }

    private fun getPlayerImage() {
        val path = "entity/player/"

        leftIdle = Texture("${path}leftIdle.png")
        leftJump = Texture("${path}leftJump.png")
        rightIdle = Texture("${path}rightIdle.png")
        rightJump = Texture("${path}rightJump.png")
    }


    override fun update() {
        autoClimb = !KeyHandler.isKeyPressed(Keys.VK_DOWN)

        hitBox.keepInBounds(world.worldBorder)

        chunks = hitBox.intersectionChunks()

        jump()

        checkIfOnGround()

        fall()

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
