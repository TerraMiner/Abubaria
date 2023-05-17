package d2t.terra.abubaria.entity.item

import CollisionHandler.checkCollision
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.hitbox.EntityHitBox
import d2t.terra.abubaria.inventory.Item
import d2t.terra.abubaria.io.graphics.drawRotatedTexture
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.entityItemSize
import d2t.terra.abubaria.world.material.MaterialSize
import kotlin.math.pow

class EntityItem(val item: Item, location: Location, pickupDelay: Int = 3000) : Entity() {

    private val deathTime = System.currentTimeMillis() + 30 * 60 * 1000
    private val canPickUpAfter = System.currentTimeMillis() + pickupDelay
    private val texture = item.type.texture!!
    private val spawnLocation = location.clone

    fun spawn() {
        autoClimb = false
        onGround = false
        dyModifier = 0.008
        maxYspeed = 1.5
        maxXspeed = 1.5

        location.setLocation(spawnLocation)
        width = entityItemSize.toDouble()
        height = entityItemSize.toDouble()
        hitBox = EntityHitBox(this, width, height)
        GamePanel.world.entities.add(this)
    }

    override fun draw(playerLoc: Location) {
        if (!GamePanel.world.entities.contains(this)) return

        val screenX = Camera.worldScreenPosX((location.x).toInt(), playerLoc)
        val modY = if (item.type.size != MaterialSize.FULL) height / item.type.size.size else .0
        val screenY = (Camera.worldScreenPosY((location.y).toInt(), playerLoc) + modY).toInt()
        val height = (height / item.type.size.size).toInt()

        val angle = (dy * 60.0).toFloat().coerceIn(-45f, 45f)

        drawRotatedTexture(
            texture.textureId,
            screenX.toDouble(),
            screenY.toDouble(),
            width,
            height.toDouble(),
            angle,
            location.direction
        )
    }

    private fun tryPickUp() {
        val dx =
            (if (ClientPlayer.location.direction === Direction.LEFT) -ClientPlayer.dx else ClientPlayer.dx) + width / 2.0
        val target = ClientPlayer.location.transfer(dx, .0)

        val distToPlayer = location.distance(target)

        if (distToPlayer < 60 && canPickUpAfter < System.currentTimeMillis()) {

            val speed = ((60 - distToPlayer) / 70.0).pow(-0.05)

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