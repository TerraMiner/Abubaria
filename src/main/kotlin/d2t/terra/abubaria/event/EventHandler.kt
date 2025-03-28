package d2t.terra.abubaria.event

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.tileSizeF
import d2t.terra.abubaria.event.EventService.registerHandler
import d2t.terra.abubaria.inventory.Item
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.entityItemSize


object EventHandler {
    init {
        registerHandler<BlockDestroyEvent> {
//            ParticleDestroy(block).initParticles()


            val x = block.x * tileSizeF + (tileSizeF - entityItemSize) / 2
            val y = block.y * tileSizeF + (tileSizeF * block.type.scale - entityItemSize) / 2f
            Item(block.type, 1).drop(Location(x, y, Direction.entries.random(), GamePanel.world), 500)

//            println(Thread.currentThread().name)
//            block.updateLightAround()
        }

        registerHandler<BlockPlaceEvent> {
//            println(Thread.currentThread().name)
//            block.updateLightAround()
        }
    }
}