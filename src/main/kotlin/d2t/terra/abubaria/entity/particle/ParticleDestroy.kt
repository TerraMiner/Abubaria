package d2t.terra.abubaria.entity.particle

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.hitbox.EntityHitBox
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.block.Block
import d2t.terra.abubaria.world.particleSize
import d2t.terra.abubaria.world.material.Material
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.random.Random
import kotlin.random.nextInt

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
        if (tiles.isEmpty() && inited) remove {}
    }

    override fun initParticles() {

        if (type.texture === null || type === Material.AIR) return

        // 4 * 4 = 16 tiles
        val particles = ConcurrentLinkedQueue<Particle>()

        height = img.width.toDouble()
        width = img.width.toDouble()

        val maxParticles = Random.nextInt(particleSize / 2..particleSize * 2)

        for (y in 0 until particleSize) {

            for (x in 0 until particleSize) {
                if (particles.size >= maxParticles) break
                if (Random.nextBoolean()) continue

                val tile = type.slices[x][y]

                val modX = x * GamePanel.tileSize / particleSize
                val modY = y * GamePanel.tileSize / particleSize

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
                                bx.toDouble() * GamePanel.tileSize + modX,
                                by.toDouble() * GamePanel.tileSize + modY, dir
                            )
                        )

                    width = particleSize.toDouble()
                    height = particleSize.toDouble()

                    autoClimb = false

                    hitBox = EntityHitBox(this, width, height)

                    GamePanel.world.entities.add(this)
                })
            }
        }

        tiles = particles
        inited = true
    }
}