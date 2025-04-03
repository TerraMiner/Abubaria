package d2t.terra.abubaria.entity.impl.item

import d2t.terra.abubaria.entity.PhysicalEntity
import d2t.terra.abubaria.entity.MovingObject
import d2t.terra.abubaria.entity.impl.ClientPlayer
import d2t.terra.abubaria.entity.type.EntityType
import d2t.terra.abubaria.geometry.box.CollisionBox
import d2t.terra.abubaria.geometry.subtract
import d2t.terra.abubaria.geometry.toVector2f
import d2t.terra.abubaria.inventory.Item
import d2t.terra.abubaria.io.graphics.Color
import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.render.RenderDimension
import d2t.terra.abubaria.io.graphics.render.Renderer
import d2t.terra.abubaria.io.graphics.render.WORLD_DEBUG_LAYER
import d2t.terra.abubaria.io.graphics.render.WORLD_ENTITY_LAYER
import d2t.terra.abubaria.location.Location

open class ItemEntity(val item: Item, location: Location, pickupDelay: Int = 3000) : PhysicalEntity(EntityType.ITEM, location),
    MovingObject {

    private val deathTime = System.currentTimeMillis() + 30 * 60 * 1000
    private val canPickUpAfter = System.currentTimeMillis() + pickupDelay

    var renderSizeX: Float = type.width
    var renderSizeY: Float = type.height * item.type.scale

    override val renderLocation = location.clone
    private var renderAngle = 0f

    override fun spawn() {
        super.spawn()
        collisionBox.sizeX = type.width
        collisionBox.sizeY = type.height * item.type.scale
    }

    override fun updateRenderLocation(location: Location, speed: Float) {
        val targetAngle = Math.toRadians((-movement.x * 60.0).coerceIn(-45.0, 45.0)).toFloat()
        renderAngle = lerp(renderAngle, targetAngle, interpSpeed(1.5f))

        if (renderSizeX != collisionBox.sizeX) {
            renderSizeX = lerp(renderSizeX, collisionBox.sizeX, interpSpeed(.02f))
        }
        if (renderSizeY != collisionBox.sizeY) {
            renderSizeY = lerp(renderSizeY, collisionBox.sizeY, interpSpeed(.02f))
        }

        super.updateRenderLocation(location, speed)
    }

    override fun draw() {
        val texture = item.type.texture ?: return
        Renderer.render(
            texture, Model.Companion.DEFAULT,
            renderLocation.x, renderLocation.y,
            renderSizeX, renderSizeY,
            renderAngle,
            zIndex = WORLD_ENTITY_LAYER,
            dim = RenderDimension.WORLD,
            ignoreCamera = false
        )
    }

    private fun tryPickUp() {
        val dx = movement.x + collisionBox.sizeX / 2.0f
        val targetLoc = ClientPlayer.centerLocation
        val targetLoc2 = centerLocation

        val target = targetLoc.transfer(dx, .0f)
        val distToPlayer = targetLoc2.distance(target)

        if (distToPlayer < 60 && canPickUpAfter < System.currentTimeMillis()) {
            val baseSpeed = 0.5f

            val speedMultiplier = 1f + (1f - distToPlayer / 60f) * 2f
            val speed = baseSpeed * speedMultiplier

            val direction = targetLoc.toVector2f().subtract(targetLoc2.toVector2f()).normalize()

            movement(direction.mul(speed))

            if (distToPlayer <= collisionBox.sizeX) {
                ClientPlayer.inventory.giveItem(item)
                remove()
            }
        }
    }

    override fun calculatePhysics() {
        tryPickUp()
        if (deathTime - System.currentTimeMillis() <= 0L) {
            remove()
            return
        }

        limitSpeed()
        tickFall()
        applyFriction()
        checkCollision()
        applyMovement()

        updateRenderLocation(location)
    }

    override fun checkCollision() {
        if (canCollideWithBlocks) {
            collideXY()
        }
    }

    override fun tryJump(futureBox: CollisionBox, intersectsHitBoxes: List<CollisionBox>) {
    }

}