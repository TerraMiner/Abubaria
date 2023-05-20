package d2t.terra.abubaria.event

import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.GamePanel.tileSizeF
import d2t.terra.abubaria.entity.particle.ParticleDestroy
import d2t.terra.abubaria.event.EventService.registerHandler
import d2t.terra.abubaria.inventory.Item
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.entityItemSize


object EventHandler {
    init {
        registerHandler<BlockDestroyEvent> {
            ParticleDestroy(block).initParticles()

            val x = block.x * tileSizeF + (tileSize - entityItemSize)/2F
            val y = block.y * tileSizeF + (block.type.height - entityItemSize)/2F
            Item(block.type,1).drop(Location(x,y, Direction.values().random()),500)

//            println(Thread.currentThread().name)
            block.updateLightAround()
        }

        registerHandler<BlockPlaceEvent> {
//            println(Thread.currentThread().name)
            block.updateLightAround()
        }
    }
}