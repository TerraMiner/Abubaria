package d2t.terra.abubaria.entity.impl

import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.entity.type.EntityType
import d2t.terra.abubaria.geometry.subtract
import d2t.terra.abubaria.geometry.toVector2f
import d2t.terra.abubaria.inventory.Item
import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.render.RendererManager
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.entityItemSize
import d2t.terra.abubaria.util.print
//import d2t.terra.abubaria.io.graphics.render.batch.BatchRenderer
import kotlin.math.pow

class ItemEntity(val item: Item, location: Location, pickupDelay: Int = 3000) : Entity(EntityType.ITEM, location) {

    private val deathTime = System.currentTimeMillis() + 30 * 60 * 1000
    private val canPickUpAfter = System.currentTimeMillis() + pickupDelay

    override fun spawn() {
        super.spawn()
        collisionBox.sizeX = entityItemSize.toFloat()
        collisionBox.sizeY = entityItemSize.toFloat() * item.type.scale
    }

    override fun draw() {
        val angle = Math.toRadians((-movement.x * 60.0).coerceIn(-45.0, 45.0)).toFloat()
        item.type.texture?.let {
            RendererManager.WorldRenderer.render(
                it, Model.Companion.DEFAULT,
                location.x.toFloat(), location.y.toFloat(),
                collisionBox.sizeX.toFloat(), collisionBox.sizeY.toFloat(),
                angle
            )
        }
    }

//    override fun addToBatch(batchRenderer: BatchRenderer) {
//        val angle = Math.toRadians((-movement.x * 60.0).coerceIn(-45.0, 45.0)).toFloat()
//        item.type.texture?.let {
//            batchRenderer.addToBatch(
//                it,
//                location.x.toFloat(), location.y.toFloat(),
//                collisionBox.sizeX.toFloat(), collisionBox.sizeY.toFloat(),
//                angle
//            )
//        }
//    }

    private fun tryPickUp() {
        val dx = movement.x + collisionBox.sizeX / 2.0f
        val targetLoc = ClientPlayer.location
        val target = targetLoc.transfer(dx, .0f)

        val distToPlayer = location.distance(target)

        if (distToPlayer < 60 && canPickUpAfter < System.currentTimeMillis()) {

            val speed = (60 - distToPlayer).pow(-0.01f)
//            println(distToPlayer)

            val direction = location.toVector2f().subtract(targetLoc).normalize().mul(-speed)

            movement(direction)

            if (distToPlayer <= ClientPlayer.collisionBox.sizeX) {
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

        applyFriction()
        limitSpeed()
        tickFall()
        applyFriction()
        checkCollision()
        applyMovement()
    }

    override fun checkCollision() {
        if (canCollideWithBlocks) {
            collideXY()
        }
    }

}