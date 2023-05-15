package d2t.terra.abubaria.entity.item

import CollisionHandler.checkCollision
import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.inventory.Item
import d2t.terra.abubaria.hitbox.EntityHitBox
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.io.graphics.drawRect
import d2t.terra.abubaria.io.graphics.drawTexture

class EntityItem(val item: Item, throwOwner: Entity?) : Entity() {

    private val entityItemSize = 12.0
    private val deathTime = System.currentTimeMillis() + 30 * 60 * 1000
    private val canPickUpAfter = System.currentTimeMillis() + 4000
    private val texture = item.type.texture!!
    private val spawnLocation = throwOwner?.location?.clone?.move(throwOwner.width / 2.0, throwOwner.height / 2.0)

    fun spawn() {
        autoClimb = false
        onGround = false
        dyModifier = 0.008
        maxYspeed = 1.5
        maxXspeed = 1.5

        location.setLocation(spawnLocation ?: Location())
        width = entityItemSize
        height = entityItemSize
        hitBox = EntityHitBox(this, width, height)
        GamePanel.world.entities.add(this)
    }

    override fun draw(playerLoc: Location) {
        if (!GamePanel.world.entities.contains(this)) return

        val screenX = Camera.worldScreenPosX((location.x).toInt(), playerLoc)
        val screenY =
            (Camera.worldScreenPosY((location.y).toInt(), playerLoc) + height * item.type.state.offset).toInt()
        val height = (height * (1.0 - item.type.state.offset)).toInt()

        drawTexture(
            texture.textureId, screenX, screenY,
            width.toInt(), height
        )

        if (Client.debugMode) {
            drawRect(screenX, screenY, hitBox.width.toInt(), height)
        }

    }

    private fun tryPickUp() {
        val target = ClientPlayer.location.clone.move(width / 2.0, height / 2.0)

        val distToPlayer = location.distance(target)

        if (distToPlayer < 60 && canPickUpAfter < System.currentTimeMillis()) {

            velocity(target,distToPlayer / 50.0)

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

        hitBox.keepInBounds(GamePanel.world.worldBorder)

        chunks = hitBox.intersectionChunks()

        applyFriction()

        checkIfOnGround()

        fall()

        checkCollision()

//        println(" after checks dx $dx")
//        println(" after checks dy $dy")

        location.move(dx,dy)

        hitBox.setLocation(location)
    }


}