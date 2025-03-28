package d2t.terra.abubaria.entity.registry

import d2t.terra.abubaria.entity.AbstractEntity
import d2t.terra.abubaria.world.Chunk
import d2t.terra.abubaria.world.World
import d2t.terra.abubaria.world.block.Position
import java.util.concurrent.ConcurrentHashMap

class EntityLevel(val world: World) {
    val sections = ConcurrentHashMap<Position, EntitySection>()

    fun getSection(chunk: Position): EntitySection? {
        return sections[chunk]
    }

    fun getEntities(chunk: Position): Set<AbstractEntity>? {
        return sections[chunk]?.entities
    }

    fun addEntity(entity: AbstractEntity, chunk: Position) {
//        if (chunk.x < 0 || chunk.x > world.worldChunkWidth) return
//        if (chunk.y < 0 || chunk.y > world.worldChunkHeight) return
        sections.getOrPut(chunk) { EntitySection() }?.addEntity(entity)
    }

    fun removeEntity(entity: AbstractEntity, chunk: Position) {
//        if (chunk.x < 0 || chunk.x > world.worldChunkWidth) return
//        if (chunk.y < 0 || chunk.y > world.worldChunkHeight) return
        sections[chunk]?.let {
            it.removeEntity(entity)
            if (it.entities.isEmpty()) {
                sections.remove(chunk)
            }
        }
    }
}