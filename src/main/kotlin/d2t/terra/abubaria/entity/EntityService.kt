package d2t.terra.abubaria.entity

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.geometry.position
import d2t.terra.abubaria.world.World
import d2t.terra.abubaria.entity.registry.EntityLevel
import java.util.concurrent.ConcurrentHashMap

object EntityService {
    
    val Entities = ConcurrentHashMap<Int, AbstractEntity>()
    val EntitiesByPositions = ConcurrentHashMap<World, EntityLevel>()

//    fun load() {

//        on<PlayerChangedWorldEvent> {
//            from.getEntitiesInWorld()?.forEach {
//                it.viewers[player] = false
//            }
//        }
//    }
    
//    fun unload() {
//    }
    
    fun register(entity: AbstractEntity) {
        Entities[entity.id] = entity
        EntitiesByPositions.getOrPut(GamePanel.world) { EntityLevel(GamePanel.world) }.addEntity(entity, entity.location.position.chunkPosition)
    }
    
    fun unregister(entity: AbstractEntity) {
        Entities.remove(entity.id)
        EntitiesByPositions[GamePanel.world]?.removeEntity(entity, entity.location.position.chunkPosition)
    }

    fun tick() {
        Entities.values.forEach {
            it.tick()
        }
    }

    fun isRegistered(entity: AbstractEntity) = Entities.containsKey(entity.id)

    val EMPTY_ARRAY = intArrayOf()
}