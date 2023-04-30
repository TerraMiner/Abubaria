package d2t.terra.abubaria.entity

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.Block
import d2t.terra.abubaria.world.tile.Material
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.random.Random

class Particle(val texture: BufferedImage, val x: Int, val y: Int, private val parentSize: Int) : Entity(Location()) {
    override fun draw(g2: Graphics2D, clientLoc: Location) {
        if (!GamePanel.world.entities.contains(this)) return

        val screenX = Camera.worldScreenPosX((location.x * tileSize).toInt(), clientLoc) - x * parentSize + parentSize/2
        val screenY = Camera.worldScreenPosY((location.y * tileSize).toInt(), clientLoc) - y * parentSize + parentSize/2

        g2.drawImage(texture, screenX , screenY , texture.width, texture.height, null)
    }
}

class ParticleDestroy(private val block: Block) : Entity(Location()) {

    private fun initParticles(): ConcurrentLinkedQueue<Particle> {

        if (block.type.texture === null || block.type === Material.AIR) return ConcurrentLinkedQueue()

        val tileSize = block.type.texture!!.width / 3 // 4 * 4 = 16 tiles
        val particles = ConcurrentLinkedQueue<Particle>()

        height = block.type.texture!!.width.toDouble()
        width = block.type.texture!!.width.toDouble()

        for (y in 0 until 3) {
            for (x in 0 until 3) {
                if (y != 0 && Random.nextInt(0,5) != 1 || particles.size > 4) continue
                val tile = block.type.texture!!.getSubimage(x * tileSize, y * tileSize, tileSize, tileSize)
                particles.add(Particle(tile, x, y, tileSize*3).apply {

                    val dir = if (x == 0) Direction.LEFT else Direction.RIGHT

                    health = 200.0

                    dx = Random.nextDouble(-.01,.01)

                    dy = -.03

                    dyModifier = 0.0006

                    maxYspeed = 0.1

                    location.setLocation(Location((block.x).toDouble() + x, (block.y).toDouble() + y, dir))

                    GamePanel.world.entities.add(this)
                })
            }
        }

        GamePanel.world.entities.add(this)
        return particles
    }

    val tiles = initParticles()

    override fun update() {
        tiles.forEach {
            if (!GamePanel.world.entities.contains(it)) tiles.remove(it)

            if (it.health <= 0) it.removed = true

            it.fall()

            it.location.x += it.dx
            it.location.y += it.dy
            it.health -= 1
        }
    }


}