package d2t.terra.abubaria.event

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.type.EntityType
import d2t.terra.abubaria.tileSizeF
import d2t.terra.abubaria.event.EventService.registerHandler
import d2t.terra.abubaria.inventory.Item
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.util.print


object EventHandler {
    init {
        registerHandler<BlockDestroyEvent> {
//            ParticleDestroy(block).initParticles()


            val x = block.collisionBox.x
            val y = block.collisionBox.y
            Item(block.type, 1).drop(Location(x, y, Direction.entries.random(), GamePanel.world), 500, 0f, 0f)
//            block.updateLightAround()
        }

        registerHandler<BlockPlaceEvent> {
//            println(Thread.currentThread().name)
//            block.updateLightAround()
        }
    }
}