package d2t.terra.abubaria.entity.registry

import d2t.terra.abubaria.entity.AbstractEntity
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.util.concurrentSetOf

class EntitySection {
    val entities = concurrentSetOf<AbstractEntity>()

    fun addEntity(entity: AbstractEntity) {
        entities.add(entity)
    }

    fun removeEntity(entity: AbstractEntity) {
        entities.remove(entity)
    }

    fun drawEntities() {
        entities.forEach { it.draw() }
    }
}