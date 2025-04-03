package d2t.terra.abubaria.entity.impl

import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.LivingEntity
import d2t.terra.abubaria.entity.MovingObject
import d2t.terra.abubaria.entity.type.EntityType
import d2t.terra.abubaria.interpBound
import d2t.terra.abubaria.inventory.Inventory
import d2t.terra.abubaria.io.devices.KeyHandler.isKeyPressed
import d2t.terra.abubaria.io.devices.Keys
import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.Texture
import d2t.terra.abubaria.io.graphics.render.RenderDimension
import d2t.terra.abubaria.io.graphics.render.Renderer
import d2t.terra.abubaria.io.graphics.render.WORLD_DEBUG_LAYER
import d2t.terra.abubaria.io.graphics.render.WORLD_PLAYER_LAYER
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location

object ClientPlayer : LivingEntity(EntityType.PLAYER, GamePanel.world.spawnLocation.clone), MovingObject {

    override val renderLocation: Location = location.clone

    //todo переписать на спрайт со сменой модельных координат
    val leftIdle = Texture.get("entity/player/leftIdle.png")
    val leftJump = Texture.get("entity/player/leftJump.png")
    val rightIdle = Texture.get("entity/player/rightIdle.png")
    val rightJump = Texture.get("entity/player/rightJump.png")

    var onJump: Boolean = false
    private var jumpStart: Long = 0L
    private var jumpEnd: Long = 0L

    var inventory = Inventory(10, 5)

    val centerPos get() = location.clone.set(collisionBox.centerX, collisionBox.centerY)

    override val baseInterpSpeed: Float = .2f

    init {
        airFriction = 0.85f
        groundFriction = 0.85f
    }

    override fun calculatePhysics() {
        limitSpeed()
        jump()
        tickFall()
        applyFriction()
        checkCollision()
        applyMovement()

        updateRenderLocation(location)
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
        val collider = collisionBox.clone()
        collideY(collider, hitBoxes)
        collideX(collider, hitBoxes)
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
        Renderer.render(
            image,
            Model.Companion.DEFAULT,
            renderLocation.x, renderLocation.y,
            type.width,
            type.height,
            zIndex = WORLD_PLAYER_LAYER,
            dim = RenderDimension.WORLD,
            ignoreCamera = false
        )

        if (Client.debugMode) {
            Renderer.renderRectangle(
                renderLocation.x, renderLocation.y,
                type.width, type.height,
                zIndex = WORLD_DEBUG_LAYER,
                dim = RenderDimension.WORLD,
                ignoreCamera = false
            )
        }
    }
}