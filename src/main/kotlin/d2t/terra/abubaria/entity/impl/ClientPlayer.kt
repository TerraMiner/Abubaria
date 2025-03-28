package d2t.terra.abubaria.entity.impl

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.EntityService
import d2t.terra.abubaria.entity.LivingEntity
import d2t.terra.abubaria.entity.type.EntityType
import d2t.terra.abubaria.geometry.box.ColliderType
import d2t.terra.abubaria.geometry.box.CollisionBox
import d2t.terra.abubaria.world.Camera
import d2t.terra.abubaria.inventory.Inventory
import d2t.terra.abubaria.io.devices.KeyHandler.isKeyPressed
import d2t.terra.abubaria.io.devices.Keys
import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.Texture
import d2t.terra.abubaria.io.graphics.render.Renderer
import d2t.terra.abubaria.io.graphics.render.RendererManager
//import d2t.terra.abubaria.io.graphics.render.batch.BatchRenderer
import d2t.terra.abubaria.io.graphics.shader.module.geometry.ShaderShapeModule
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.tileSizeF

object ClientPlayer : LivingEntity(EntityType.PLAYER, GamePanel.world.spawnLocation.clone) {

    //todo переписать на спрайт со сменой модельных координат
    val leftIdle = Texture("entity/player/leftIdle.png")
    val leftJump = Texture("entity/player/leftJump.png")
    val rightIdle = Texture("entity/player/rightIdle.png")
    val rightJump = Texture("entity/player/rightJump.png")

    var onJump: Boolean = false
    private var jumpStart: Long = 0L
    private var jumpEnd: Long = 0L

    var inventory = Inventory(10, 5)

    val centerPos get() = location.clone.set(collisionBox.centerX, collisionBox.centerY)

    init {
        EntityService.register(this)
    }

    override fun calculatePhysics() {
        limitSpeed()
        jump()
        tickFall()
        applyFriction()
        checkCollision()
        applyMovement()
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

    override fun collideXY() {
        val expandedBox = collisionBox.clone().expand(movement)
        val hitBoxes = expandedBox.getCollidingBlocks(location.world)
        collideY(hitBoxes)
        collideX(hitBoxes)
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

    override fun draw() {
        val image = when (location.direction) {
            Direction.LEFT -> {
                if (isOnGround) leftIdle
                else leftJump
            }

            Direction.RIGHT -> {
                if (isOnGround) rightIdle
                else rightJump
            }
        }


        val offX = Camera.playerScreenPosX(location)
        val offY = Camera.playerScreenPosY(location)

        RendererManager.UIRenderer.render(
            image,
            Model.Companion.DEFAULT,
            offX,
            offY,
            type.width,
            type.height
        )
    }

//    override fun addToBatch(batchRenderer: BatchRenderer) {
//        val image = when (location.direction) {
//            Direction.LEFT -> {
//                if (isOnGround) leftIdle
//                else leftJump
//            }
//
//            Direction.RIGHT -> {
//                if (isOnGround) rightIdle
//                else rightJump
//            }
//        }
//
//
//        val offX = Camera.playerScreenPosX(location)
//        val offY = Camera.playerScreenPosY(location)
//
//        batchRenderer.addToBatch(
//            image,
//            offX,
//            offY,
//            type.width,
//            type.height
//        )
//    }

    override fun spawn() {}
}