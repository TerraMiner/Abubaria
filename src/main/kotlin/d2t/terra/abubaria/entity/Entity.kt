package d2t.terra.abubaria.entity

import d2t.terra.abubaria.io.devices.KeyHandler
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.hitbox.EntityHitBox
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.Chunk
import d2t.terra.abubaria.world.material.Material
import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.Texture

private var id = 0

open class Entity {

    open val location = Location()
    val entityId = ++id

    var removed = false

    var dxModifier = 0.05f
    var dyModifier = 0.03f

    var maxXspeed = 1.0f
    var maxYspeed = 7.0f

    var height: Float = 2.9f //
    var width: Float = 1.4f // 22pix
    var leftIdle: Texture? = null
    var leftJump: Texture? = null
    var rightIdle: Texture? = null
    var rightJump: Texture? = null

    var dx = .0f
    var dy = .0f

    var autoClimb = true

    var jumpStart = System.currentTimeMillis()
    var jumpEnd = System.currentTimeMillis() + 1000L

    var jumpHeight = .2f
    val jumpMod = .01f

    var onJump = false
    var onGround = false
    var onWorldBorder = false

    var ground = Material.AIR

    var health = 100.0f
    var maxHealth = 100.0f

    var chunks = mutableListOf<Chunk>()

    var hitBox = EntityHitBox(this, .0f, .0f)


    fun moveLeft() {
        location.direction = Direction.LEFT
        dx -= dxModifier
    }

    fun moveRight() {
        location.direction = Direction.RIGHT
        dx += dxModifier
    }

    fun applyFriction() {
        if (location.direction === Direction.LEFT) {
            if (dx < 0) dx += ground.friction
            if (dx > 0) dx = .0f
            if (dx < -maxXspeed) dx = -maxXspeed
        }

        if (location.direction === Direction.RIGHT) {
            if (dx > 0) dx -= ground.friction
            if (dx < 0) dx = .0f
            if (dx > maxXspeed) dx = maxXspeed
        }
    }

    fun fall() {

        if (onGround) {
            dy = .0f
            return
        }

        dy += dyModifier

        if (dy > maxYspeed) dy = maxYspeed

    }

    open fun update() {

    }

    open fun draw() {

    }

    fun remove(unit: () -> Unit) {
        unit.invoke()
        removed = true
    }

    fun checkIfOnGround() {
        var isOnGround = false

        if (dy == .0f)
            chunks.forEach chunks@{ chunk ->
                chunk.blockMap.forEach {
                    if (it.hitBox.top <= hitBox.bottom
                        && hitBox.bottom - it.hitBox.top == .0f
                        && it.type.collideable
                        && hitBox.run { x < it.hitBox.x + it.hitBox.width && x + width - 1 > it.hitBox.x }
                    ) {
                        isOnGround = true
                        ground = it.type
                        return@forEach
                    }
                    if (isOnGround) return@forEach
                }
                if (isOnGround) return@chunks
            }



        onGround = isOnGround
        if (!isOnGround) ground = Material.AIR
    }

    fun velocity(target: Location, modX: Float, modY: Float) {

        dx = if (target.x < location.x) -modX else if (target.x > location.x) modX else .0f
        dy = if (target.y < location.y) -modY else if (target.y > location.y) modY else .0f

        location.direction = if (dx >= 0) Direction.RIGHT else Direction.LEFT
    }

}