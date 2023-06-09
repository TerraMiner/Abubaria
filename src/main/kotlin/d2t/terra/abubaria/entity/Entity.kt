package d2t.terra.abubaria.entity

import KeyHandler
import java.awt.image.BufferedImage
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.EntityHitBox
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.Chunk
import d2t.terra.abubaria.world.tile.Material
import lwjgl.Image
import java.awt.Graphics2D
import java.nio.ByteBuffer

open class Entity(val location: Location) {

    var removed = false

    var dxModifier = 0.05
    var dyModifier = 0.03

    var maxXspeed = 1.0
    var maxYspeed = 7.0

    var height: Double = 2.9
    var width: Double = 1.4
    var leftIdle: Image? = null
    var leftJump: Image? = null
    var rightIdle: Image? = null
    var rightJump: Image? = null

    var dx = .0
    var dy = .0

    var autoClimb = true

    var jumpStart = System.currentTimeMillis()
    var jumpEnd = System.currentTimeMillis() + 1000L

    var jumpHeight = .2
    val jumpMod = .01

    var onJump = false
    var onGround = false
    var onWorldBorder = false

    var ground = Material.AIR

    var health = 100.0
    var maxHealth = 100.0

    var hitBox = EntityHitBox(this)

    var chunks = mutableListOf<Chunk>()

    fun moveLeft() {
        dx -= dxModifier
//        Camera.cameraDx += dxModifier*6
    }

    fun moveRight() {
        dx += dxModifier
//        Camera.cameraDx -= dxModifier*6
    }


    open fun applyMovement() {

        when (location.direction) {
            Direction.LEFT -> if (KeyHandler.leftPressed) moveLeft()
            Direction.RIGHT -> if (KeyHandler.rightPressed) moveRight()
        }

        if (location.direction === Direction.LEFT) {

            if (dx < 0) dx += ground.friction
            if (dx > 0) dx = .0
            if (dx < -maxXspeed) dx = -maxXspeed
        }

        if (location.direction === Direction.RIGHT) {

            if (dx > 0) dx -= ground.friction
            if (dx < 0) dx = .0
            if (dx > maxXspeed) dx = maxXspeed
        }

    }

    fun fall() {

        if (onGround) {
            dy = .0
            return
        }

        dy += dyModifier

//        Camera.cameraDy -= dyModifier*5

        if (dy > maxYspeed) dy = maxYspeed

    }

    open fun update() {

    }

    open fun draw(g2: Graphics2D, location: Location) {

    }

    fun checkIfOnGround() {
        var isOnGround = false

        if (dy == .0)
            chunks.forEach chunks@{ chunk ->
                chunk.blocks.forEach blocksCols@{ blockCols ->
                    blockCols.forEach blocks@{
                        if (it.hitBox.top <= hitBox.bottom && hitBox.bottom - it.hitBox.top == .0 && it.type.collideable
                            && hitBox.run { x < it.hitBox.x + it.hitBox.width && x + width - 1 > it.hitBox.x }
                        ) {
                            isOnGround = true
                            ground = it.type
                            return@blocks
                        }
                    }

                    if (isOnGround) return@blocksCols
                }
                if (isOnGround) return@chunks
            }



        onGround = isOnGround
        if (!isOnGround) ground = Material.AIR
    }

}