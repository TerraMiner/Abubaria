package d2t.terra.abubaria.entity

import CollisionHandler.checkCollision
import CollisionHandler.checkIfStuck
import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.EntityHitBox
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.Block
import d2t.terra.abubaria.world.tile.Material
import lwjgl.Image
import lwjgl.drawRect
import lwjgl.drawTexture
import java.lang.Math.pow
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.pow
import kotlin.random.Random

const val particleSize = 8

class Particle(
    private val texture: Image,
    val x: Int,
    val y: Int,
    val owner: ParticleOwner
) : Entity(Location()) {

    override fun draw(playerLoc: Location) {
        if (!GamePanel.world.entities.contains(this)) return

        val screenX = Camera.worldScreenPosX((location.x).toInt(), playerLoc)
        val screenY = Camera.worldScreenPosY((location.y).toInt(), playerLoc)

        drawTexture(texture.textureId, screenX, screenY, tileSize / particleSize, tileSize / particleSize)

        if (Client.debugMode) {
            drawRect(screenX,screenY,hitBox.width.toInt(), hitBox.height.toInt())
        }

    }

    override fun update() {
        if (!GamePanel.world.entities.contains(this)) owner.tiles.remove(this)

        if (this.health <= 0) this.removed = true

        this.autoClimb = false

        hitBox.keepInBounds(GamePanel.world.worldBorder)

        chunks = hitBox.intersectionChunks()

        checkIfOnGround()

        if (onGround) dx = .0

        fall()

        checkCollision()

        if (checkIfStuck(hitBox)) {
            dy = .0
            dx = .0
        }

        location.x += dx
        location.y += dy

        health -= if (onGround) 2 else 1

        hitBox.setLocation(location)
    }
}

open class ParticleOwner : Entity(Location()) {
    open fun initParticles() {}

    var tiles = ConcurrentLinkedQueue<Particle>()
}

class ParticleDestroy(block: Block) : ParticleOwner() {

    var inited = false

    val bx = block.x
    val by = block.y
    val type = block.type
    val img = type.texture!!

    init {
        GamePanel.world.entities.add(this)
    }

    override fun initParticles() {

        if (type.texture === null || type === Material.AIR) return

        // 4 * 4 = 16 tiles
        val particles = ConcurrentLinkedQueue<Particle>()

        height = img.width.toDouble()
        width = img.width.toDouble()

        for (y in 0 until particleSize) {
            for (x in 0 until particleSize) {

                val tile = type.slices[x][y]

                val modX = x * tileSize / particleSize
                val modY = y * tileSize / particleSize

                particles.add(Particle(tile, x, y, this).apply {

                    val dir = if (x == 0) Direction.LEFT else Direction.RIGHT

                    health = 400.0

                    dx = Random.nextDouble(-.3, .3)

                    dy = Random.nextDouble(-.3,.3)

                    dyModifier = 0.008

                    maxYspeed = 2.0

                    location
                        .setLocation(Location(
                                bx.toDouble() * tileSize + modX,
                                by.toDouble() * tileSize + modY,
                                dir))

                    width = particleSize.toDouble().pow(-1)
                    height = particleSize.toDouble().pow(-1)

                    hitBox = EntityHitBox(this)

                    GamePanel.world.entities.add(this)
                })
            }
        }

        tiles = particles
        inited = true
    }
}