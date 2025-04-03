package d2t.terra.abubaria.entity

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.type.EntityType
import d2t.terra.abubaria.geometry.position
import d2t.terra.abubaria.location.Location

abstract class Entity(val type: EntityType, val location: Location) {

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
    val centerLocation get() = location.transfer(type.width / 2, type.height / 2)

    abstract fun spawn()
    abstract fun remove()
    abstract fun tick()
    abstract fun draw()

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

        GamePanel.world.getChunk(oldChunk)?.removeEntity(this)
        GamePanel.world.getChunk(newChunk)?.addEntity(this)

        isChunkDirty = false
        latestChunkSnapshot = newChunk
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Entity) return false

        if (id != other.id) return false

        return true
    }

    companion object {
        var entityIdCounter = 0
        val nextEntityId get() = entityIdCounter++
    }
}