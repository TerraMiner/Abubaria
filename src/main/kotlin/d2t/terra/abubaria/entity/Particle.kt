package d2t.terra.abubaria.entity

import CollisionHandler.checkCollision
import CollisionHandler.checkIfStuck
import d2t.terra.abubaria.Client
import d2t.terra.abubaria.Client.currentZoom
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.EntityHitBox
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.Block
import d2t.terra.abubaria.world.tile.Material
import d2t.terra.abubaria.lwjgl.Image
import d2t.terra.abubaria.lwjgl.drawRect
import d2t.terra.abubaria.lwjgl.drawTexture
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.pow
import kotlin.random.Random
import kotlin.random.nextInt

const val particleSize = 4

class Particle(
    private val texture: Image,
    val x: Int,
    val y: Int,
    val owner: ParticleOwner
) : Entity() {

    private val dSize = tileSize.toDouble() / particleSize.toDouble()

    override fun draw(playerLoc: Location) {
        if (!GamePanel.world.entities.contains(this)) return

        val mod = (health / maxHealth).coerceIn(.0, 1.0)

        val screenX = Camera.worldScreenPosX((location.x).toInt(), playerLoc) + (dSize / 2.0 * (1.0 - mod)).toInt()
        val screenY = Camera.worldScreenPosY((location.y).toInt(), playerLoc) + (dSize * (1.0 - mod)).toInt()

        drawTexture(texture.textureId, screenX, screenY, (dSize * mod).toInt(), (dSize * mod).toInt())

        if (Client.debugMode) {
            drawRect(screenX, screenY, hitBox.width.toInt(), hitBox.height.toInt())
        }

    }

    override fun update() {
        if (this.health <= 0) remove{
            owner.tiles.remove(this)
        }

        hitBox.keepInBounds(GamePanel.world.worldBorder)

        chunks = hitBox.intersectionChunks()

        checkIfOnGround()

        if (onGround) dx = .0

        fall()

        checkCollision()

        if (checkIfStuck(hitBox)) {
            dy = .0; dx = .0
        }

        location.x += dx
        location.y += dy

        health -= if (onGround) 2 else 1

        hitBox.setLocation(location)
    }
}

open class ParticleOwner : Entity() {
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

    override fun update() {
        if (tiles.isEmpty() && inited) remove{}
    }

    override fun initParticles() {

        if (type.texture === null || type === Material.AIR) return

        // 4 * 4 = 16 tiles
        val particles = ConcurrentLinkedQueue<Particle>()

        height = img.width.toDouble()
        width = img.width.toDouble()

        val maxParticles = Random.nextInt(particleSize/2..particleSize*2)

        for (y in 0 until particleSize) {

            for (x in 0 until particleSize) {
                if (particles.size >= maxParticles) break
                if (Random.nextBoolean()) continue

                val tile = type.slices[x][y]

                val modX = x * tileSize / particleSize
                val modY = y * tileSize / particleSize

                particles.add(Particle(tile, x, y, this).apply {

                    val dir = if (x == 0) Direction.LEFT else Direction.RIGHT

                    health = 200.0
                    maxHealth = 200.0

                    dx = Random.nextDouble(-.3, .3)

                    dy = Random.nextDouble(-.3, .3)

                    dyModifier = 0.008

                    maxYspeed = 2.0

                    location
                        .setLocation(
                            Location(
                                bx.toDouble() * tileSize + modX,
                                by.toDouble() * tileSize + modY,
                                dir
                            )
                        )

                    width = particleSize.toDouble().pow(-1)
                    height = particleSize.toDouble().pow(-1)

                    autoClimb = false

                    hitBox = EntityHitBox(this)

                    GamePanel.world.entities.add(this)
                })
            }
        }

        tiles = particles
        inited = true
    }
}