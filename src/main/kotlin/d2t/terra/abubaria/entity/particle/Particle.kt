package d2t.terra.abubaria.entity.particle

import CollisionHandler.checkCollision
import CollisionHandler.checkIfStuck
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.io.graphics.Image
import d2t.terra.abubaria.io.graphics.drawTexture
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.particleSize

class Particle(
    private val texture: Image, val x: Int, val y: Int, val owner: ParticleOwner
) : Entity() {

    private val dSize = tileSize.toDouble() / particleSize.toDouble()
    private val mod get() = (health / maxHealth).coerceIn(.0, 1.0)

    override fun draw(playerLoc: Location) {
        if (!GamePanel.world.entities.contains(this)) return

        val screenX = Camera.worldScreenPosX((location.x).toInt(), playerLoc) + (dSize / 2.0 * (1.0 - mod)).toInt()
        val screenY = Camera.worldScreenPosY((location.y).toInt(), playerLoc) + (dSize * (1.0 - mod)).toInt()

        drawTexture(texture.textureId, screenX, screenY, (dSize * mod).toInt(), (dSize * mod).toInt())
    }

//    override fun drawHitBox(playerLoc: Location) {
//        if (!GamePanel.world.entities.contains(this)) return
//
//        val screenX = Camera.worldScreenPosX((location.x).toInt(), playerLoc) + (dSize / 2.0 * (1.0 - mod)).toInt()
//        val screenY = Camera.worldScreenPosY((location.y).toInt(), playerLoc) + (dSize * (1.0 - mod)).toInt()
//
//        drawRect(screenX, screenY, hitBox.width.toInt(), hitBox.height.toInt())
//    }

    override fun update() {
        if (this.health <= 0) remove {
            owner.tiles.remove(this)
        }

//        hitBox.keepInBounds(GamePanel.world.worldBorder)

        chunks = hitBox.intersectionChunks()

        checkIfOnGround()

        if (onGround) dx = .0

        fall()

        checkCollision()

        hitBox.keepInBounds(GamePanel.world.worldBorder)

        if (checkIfStuck(hitBox)) {
            dy = .0; dx = .0
        }

        location.x += dx
        location.y += dy

        health -= if (onGround) 2 else 1

        hitBox.setLocation(location)
    }
}

