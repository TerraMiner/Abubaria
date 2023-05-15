package d2t.terra.abubaria.event

import d2t.terra.abubaria.GamePanel.tileSize
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

            val x = block.x * tileSize + (tileSize - entityItemSize)/2
            val y = block.y * block.type.height + (block.type.height - entityItemSize)/2
            Item(block.type,1).drop(Location(x,y, Direction.values().random()))

            block.updateLightAround()
        }

        registerHandler<BlockPlaceEvent> {
            block.updateLightAround()
        }
    }
}