package d2t.terra.abubaria.entity

import KeyHandler
import d2t.terra.abubaria.GamePanel.player
import java.awt.image.BufferedImage
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.EntityHitBox
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.Chunk
import d2t.terra.abubaria.world.tile.Material

open class Entity(val location: Location) {

    var dxModifier = 0.05
    var dyModifier = 0.03

    var maxXspeed = 1.0
    var maxYspeed = 7.0

    var height: Double = 2.9
    var width: Double = 1.4
    var leftIdle: BufferedImage? = null
    var leftJump: BufferedImage? = null
    var rightIdle: BufferedImage? = null
    var rightJump: BufferedImage? = null

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

    var hitBox = EntityHitBox(this)

    var chunks = mutableListOf<Chunk>()

    fun moveLeft() {
        dx -= dxModifier
    }

    fun moveRight() {
        dx += dxModifier
    }


    fun applyMovement() {

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

        if (dy > maxYspeed) dy = maxYspeed

        location.y += dy
    }

    fun checkIfOnGround() {
        var isOnGround = false

        if (dy == .0)
            player.chunks.forEach chunks@{ chunk ->
                chunk.blocks.forEach blocksCols@{ blockCols ->
                    blockCols.forEach blocks@{
                        if (it.hitBox.top <= hitBox.bottom && hitBox.bottom - it.hitBox.top == .0 && it.material.collideable
                            && hitBox.run { x < it.hitBox.x + it.hitBox.width && x + width - 1 > it.hitBox.x }
                        ) {
                            isOnGround = true
                            ground = it.material
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