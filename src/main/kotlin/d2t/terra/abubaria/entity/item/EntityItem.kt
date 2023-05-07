package d2t.terra.abubaria.entity.item

import CollisionHandler.checkCollision
import CollisionHandler.checkIfStuck
import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.inventory.Item
import d2t.terra.abubaria.location.EntityHitBox
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.lwjgl.drawRect
import d2t.terra.abubaria.lwjgl.drawTexture
import kotlin.math.pow

val entityItemSize = 12

class EntityItem(val item: Item, val throwOwner: Entity?) : Entity() {

    private val deathTime = System.currentTimeMillis() + 10000//30*60*1000
    private val canPickUpAfter = System.currentTimeMillis() + 2000
    private val texture = item.type.texture!!

    fun spawn() {
        autoClimb = false
        dyModifier = 0.008
        maxYspeed = 2.0
        location.setLocation(throwOwner?.location?.clone ?: Location())
        width = (entityItemSize).toDouble()
        height = (entityItemSize).toDouble()
        hitBox = EntityHitBox(this).apply {
            width = entityItemSize.toDouble()
            height = entityItemSize.toDouble()
        }
        GamePanel.world.entities.add(this)
    }

    override fun draw(playerLoc: Location) {
        if (!GamePanel.world.entities.contains(this)) return

        val screenX = Camera.worldScreenPosX((location.x).toInt(), playerLoc)
        val screenY = Camera.worldScreenPosY((location.y).toInt(), playerLoc)

        drawTexture(
            texture.textureId, screenX, screenY,
            width.toInt(), height.toInt(),
        )

        if (Client.debugMode) {
            drawRect(screenX, screenY, hitBox.width.toInt(), hitBox.height.toInt())
        }

    }

    private fun tryPickUp() {
        if (location.distance(ClientPlayer.location) > 64) return
//        println("trying to pickup")
    }

    override fun update() {

        tryPickUp()

        if (deathTime - System.currentTimeMillis() <= 0L) {
            remove{
                println("removed")
            }
            return
        }

        hitBox.keepInBounds(GamePanel.world.worldBorder)

        chunks = hitBox.intersectionChunks()

        checkIfOnGround()

        fall()

        applyMovement()

        checkCollision()

//        if (checkIfStuck(hitBox)) {
//            dy = .0; dx = .0
//        }

        location.x += dx
        location.y += dy

        hitBox.setLocation(location)
    }


}