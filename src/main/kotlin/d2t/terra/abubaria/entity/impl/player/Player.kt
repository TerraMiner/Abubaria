package d2t.terra.abubaria.entity.impl.player

import d2t.terra.abubaria.Client
import d2t.terra.abubaria.entity.LivingEntity
import d2t.terra.abubaria.entity.impl.item.ItemEntity
import d2t.terra.abubaria.entity.type.EntityType
import d2t.terra.abubaria.inventory.Inventory
import d2t.terra.abubaria.io.graphics.texture.Model
import d2t.terra.abubaria.io.graphics.texture.Texture
import d2t.terra.abubaria.io.graphics.render.Layer
import d2t.terra.abubaria.io.graphics.render.RenderDimension
import d2t.terra.abubaria.io.graphics.render.Renderer
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location

open class Player(location: Location) : LivingEntity(EntityType.PLAYER, location) {
    //todo переписать на спрайт со сменой модельных координат
    val leftIdle = Texture.get("entity/player/leftIdle.png")
    val leftJump = Texture.get("entity/player/leftJump.png")
    val rightIdle = Texture.get("entity/player/rightIdle.png")
    val rightJump = Texture.get("entity/player/rightJump.png")

    var inventory = Inventory(10, 5)

    val centerPos get() = location.clone.set(collisionBox.centerX, collisionBox.centerY)

    init {
        airFriction = 0.85f
        groundFriction = 0.85f
    }

    open fun tryPickupItems() {
        getNearbyEntities<ItemEntity>(40f,40f).forEach(ItemEntity::tryPickUp)
    }

    override fun calculatePhysics() {
        limitSpeed()
        tickFall()
        applyFriction()
        checkCollision()
        applyMovement()
        tryPickupItems()
    }

    override fun collideXY() {
        val expandedBox = collisionBox.clone().expand(movement)
        val hitBoxes = expandedBox.getCollidingBlocks(location.world)
        val collider = collisionBox.clone()
        collideY(collider, hitBoxes)
        collideX(collider, hitBoxes)
    }

    override fun draw() {
        val location = location.clone
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
            location.x, location.y,
            type.width,
            type.height,
            Layer.WORLD_PLAYER_LAYER,
            RenderDimension.WORLD,
            ignoreZoom = false
        )

        if (Client.showWorldGrid) {
            Renderer.renderRectangle(
                location.x, location.y,
                type.width, type.height,
                Layer.WORLD_DEBUG_LAYER,
                RenderDimension.WORLD,
                ignoreZoom = false
            )
        }
    }

}