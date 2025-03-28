package d2t.terra.abubaria.entity

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.type.EntityType
import d2t.terra.abubaria.geometry.position
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.entity.registry.EntityLevel
//import d2t.terra.abubaria.io.graphics.render.batch.BatchRenderer

abstract class AbstractEntity(val type: EntityType, val location: Location) {

    val id = nextEntityId

    var isRemoved = false
        private set
    var isChunkDirty = false
        private set

    var isOnGround = false

    var hasCollision = true

    var latestChunkSnapshot = location.position.chunkPosition
        private set

    var hasGravity = true

    val oldLocation = Location(0, 0)

    abstract fun spawn()
    abstract fun remove()
    abstract fun tick()
    abstract fun draw()
//    abstract fun addToBatch(batchRenderer: BatchRenderer)

    open fun teleport(
        x: Float = location.x,
        y: Float = location.y,
    ) {
        updatePosition(x,y)
    }

    fun teleport(target: Location) {
        teleport(target.x, target.y)
    }

    open fun updatePosition(x: Float, y: Float) {
        oldLocation.set(location)

        location.set(x, y)

        val oldChunk = latestChunkSnapshot
        val newChunk = location.position.chunkPosition

        isChunkDirty = oldChunk != newChunk

        if (!isChunkDirty) return

        EntityService.EntitiesByPositions[GamePanel.world]?.removeEntity(this, oldChunk)
        EntityService.EntitiesByPositions.getOrPut(GamePanel.world) { EntityLevel(GamePanel.world) }.addEntity(this, newChunk)

        isChunkDirty = false
        latestChunkSnapshot = newChunk
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractEntity) return false

        if (id != other.id) return false

        return true
    }

    companion object {
        var entityIdCounter = 0
        val nextEntityId get() = entityIdCounter++
    }
}