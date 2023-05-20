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

        height = img.height.toFloat()
        width = img.width.toFloat()

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

                    health = 200.0F
                    maxHealth = 200.0F

                    dx = Random.nextDouble(-.3, .3).toFloat()

                    dy = Random.nextDouble(-.3, .3).toFloat()

                    dyModifier = 0.008F

                    maxYspeed = 2.0F

                    location
                        .setLocation(
                            Location(
                                bx * GamePanel.tileSize + modX,
                                by * GamePanel.tileSize + modY, dir
                            )
                        )

                    width = particleSize.toFloat()
                    height = particleSize.toFloat()

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