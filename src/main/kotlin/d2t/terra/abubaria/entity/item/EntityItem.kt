package d2t.terra.abubaria.entity.item

import CollisionHandler.checkCollision
import CollisionHandler.checkIfStuck
import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.inventory.Item
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.EntityHitBox
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.lwjgl.drawRect
import d2t.terra.abubaria.lwjgl.drawTexture
import d2t.terra.abubaria.world.tile.Material
import kotlin.math.pow

class EntityItem(val item: Item, val throwOwner: Entity?) : Entity() {

    private val entityItemSize = 12.0
    private val deathTime = System.currentTimeMillis() + 30*60*1000
    private val canPickUpAfter = System.currentTimeMillis() + 2000
    private val texture = item.type.texture!!
    private val spawnLocation = throwOwner?.location?.clone?.move(throwOwner.width/2.0,throwOwner.height/2.0)

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
        val screenY = (Camera.worldScreenPosY((location.y).toInt(), playerLoc) + height * item.type.state.offset).toInt()
        val height = (height * (1.0 - item.type.state.offset)).toInt()

        drawTexture(
            texture.textureId, screenX, screenY,
            width.toInt(), height,
        )

        if (Client.debugMode) {
            drawRect(screenX, screenY, hitBox.width.toInt() , height)
        }

    }

    private fun tryPickUp() {
        if (location.distance(ClientPlayer.location) > 64) return
    }

    override fun update() {

        tryPickUp()

        if (deathTime - System.currentTimeMillis() <= 0L) {
            remove{}
            return
        }

        hitBox.keepInBounds(GamePanel.world.worldBorder)

        chunks = hitBox.intersectionChunks()

        checkIfOnGround()

        if (onGround) dx = .0

        fall()

        checkCollision()

        location.x += dx
        location.y += dy

        applyMovement()

        hitBox.setLocation(location)
    }


}