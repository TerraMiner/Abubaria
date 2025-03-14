package d2t.terra.abubaria.entity.item

import CollisionHandler.checkCollision
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.hitbox.EntityHitBox
import d2t.terra.abubaria.inventory.Item
import d2t.terra.abubaria.io.graphics.render.RendererManager
import d2t.terra.abubaria.io.graphics.render.WorldRenderer
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.entityItemSize
import d2t.terra.abubaria.io.graphics.Model
import kotlin.math.pow

class EntityItem(val item: Item, location: Location, pickupDelay: Int = 3000) : Entity() {

    private val deathTime = System.currentTimeMillis() + 30 * 60 * 1000
    private val canPickUpAfter = System.currentTimeMillis() + pickupDelay
//    private val texture = item.type.image!!
    private val spawnLocation = location.clone

    fun spawn() {
        autoClimb = false
        onGround = false
        dyModifier = 0.008f
        maxYspeed = 1.5f
        maxXspeed = 1.5f

        location.setLocation(spawnLocation)
        width = entityItemSize.toFloat()
        height = entityItemSize.toFloat() * item.type.scale
        hitBox = EntityHitBox(this, width, height)
        GamePanel.world.entities.add(this)
    }

    override fun draw() {
        if (!GamePanel.world.entities.contains(this)) return
        val angle = Math.toRadians((-dx * 60.0).coerceIn(-45.0, 45.0)).toFloat()
        item.type.texture?.let { RendererManager.WorldRenderer.render(it, Model.DEFAULT, location.x, location.y, width, height, angle) }
    }

    private fun tryPickUp() {
        val dx =
            (if (location.direction === Direction.LEFT) -dx else dx) + width / 2.0f
        val target = location.transfer(dx, .0f)

        val distToPlayer = location.distance(target)

        if (distToPlayer < 60 && canPickUpAfter < System.currentTimeMillis()) {

            val speed = ((60 - distToPlayer) / 70.0f).pow(-0.05f)

            velocity(target, speed, speed)

            if (distToPlayer <= width) {
                ClientPlayer.inventory.giveItem(item)
                remove {}
            }
        }
    }

    override fun update() {

        tryPickUp()

        if (deathTime - System.currentTimeMillis() <= 0L) {
            remove {}
            return
        }

        chunks = hitBox.intersectionChunks()

        applyFriction()

        checkIfOnGround()

        fall()

        checkCollision()

        hitBox.keepInBounds(GamePanel.world.worldBorder)

//        println(" after checks dx $dx")
//        println(" after checks dy $dy")

        location.move(dx, dy)

        hitBox.setLocation(location)
    }


}