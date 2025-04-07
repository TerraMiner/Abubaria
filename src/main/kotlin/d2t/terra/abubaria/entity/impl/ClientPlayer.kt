package d2t.terra.abubaria.entity.impl

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.impl.player.Player
import d2t.terra.abubaria.io.devices.KeyHandler.isKeyPressed
import d2t.terra.abubaria.io.devices.Keys

object ClientPlayer : Player(GamePanel.world.spawnLocation.clone) {

    var onJump: Boolean = false
    private var jumpStart: Long = 0L
    private var jumpEnd: Long = 0L

    init {
        airFriction = 0.85f
        groundFriction = 0.85f
    }

    override fun calculatePhysics() {
        jump()
        super.calculatePhysics()
    }

    override fun checkCollision() {
        if (canCollideWithBlocks) {
            if (!isKeyPressed(Keys.VK_DOWN)) {
                val futureBox = collisionBox.clone().expandX(movement.x)
                val forClimb = futureBox.getCollidingBlocks(location.world)
                tryClimb(futureBox, forClimb)
            }
            collideXY()
        }
    }

    fun jump() {
        val time = System.currentTimeMillis()
        val isSpacePressed = isKeyPressed(Keys.VK_SPACE)
        val jumpTime = 400L

        if (isOnGround && isSpacePressed) {
            jumpStart = time
            jumpEnd = time + jumpTime
        }

        if (!isSpacePressed && jumpEnd - jumpStart == jumpTime) jumpEnd = time
        onJump = time >= jumpStart && time <= jumpEnd

        if (onJump) {
            val mod = (System.currentTimeMillis() - jumpStart).toFloat() / 17f
            val bound = 1f
            movement.y -= jumpHeight / if (mod < bound) bound else mod
        }
    }
}