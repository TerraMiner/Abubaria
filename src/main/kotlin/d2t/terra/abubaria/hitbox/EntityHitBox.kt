package d2t.terra.abubaria.hitbox

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.world.Chunk
import kotlin.math.min

class EntityHitBox(val entity: Entity, width: Float, height: Float) :
    HitBox(entity.location, width, height) {

    val clone
        get() = EntityHitBox(entity, width, height).also {
            it.x = x
            it.y = y
        }

    override fun move(dx: Float, dy: Float): EntityHitBox {
        x += dx
        y += dy
        return this
    }

    fun intersectionChunks(): MutableList<Chunk> {
        val chunks = mutableListOf<Chunk>()

        val leftPos = ((left + entity.dx - tileSize) / tileSize).toInt()
        val rightPos = ((right + entity.dx + tileSize) / tileSize).toInt()

        val topPos = ((top + entity.dy - tileSize) / tileSize).toInt()
        val bottomPos = ((bottom + entity.dy + tileSize) / tileSize).toInt()

        val leftTopChunk = GamePanel.world.getChunkAt(leftPos, topPos)
        val leftBottomChunk = GamePanel.world.getChunkAt(leftPos, bottomPos)

        val rightTopChunk = GamePanel.world.getChunkAt(rightPos, topPos)
        val rightBottomChunk = GamePanel.world.getChunkAt(rightPos, bottomPos)

        if (leftTopChunk != null && !chunks.contains(leftTopChunk)) chunks.add(leftTopChunk)
        if (leftBottomChunk != null && !chunks.contains(leftBottomChunk)) chunks.add(leftBottomChunk)
        if (rightTopChunk != null && !chunks.contains(rightTopChunk)) chunks.add(rightTopChunk)
        if (rightBottomChunk != null && !chunks.contains(rightBottomChunk)) chunks.add(rightBottomChunk)
        return chunks
    }

    fun keepInBounds(other: HitBox) {
        var isOnWorldBorder = false
        if (x < other.x) {
            x = other.x
            entity.dx = 0F
            entity.location.x = x
        } else if (x + width > other.x + other.width) {
            x = other.x + other.width - width
            entity.dx = 0F
            entity.location.x = x
        }

        if (y < other.y) {
            y = other.y
            entity.dy = 0F
            entity.location.y = y
        } else if (y + height > other.y + other.height) {
            y = other.y + other.height - height
            entity.dy = 0F
            entity.location.y = y
            isOnWorldBorder = true
        }
        entity.onWorldBorder = isOnWorldBorder
    }

    fun pushOutX(other: HitBox) {
        val dx1 = ((x + width) - other.x)
        val dx2 = ((other.x + other.width) - x)

        if (entity.location.direction === Direction.RIGHT) {
            entity.dx = 0F
            x -= dx1
        } else {
            entity.dx = 0F
            x += dx2
        }

    }

    fun pushOutY(other: HitBox) {
        val dy1 = ((y + height) - other.y)
        val dy2 = ((other.y + other.height) - y)

        if (dy1 < dy2) {
            y -= dy1
            entity.dy = 0F
            entity.location.y = y
        } else if (dy2 < dy1) {
            y += dy2
            entity.dy = 0F
            entity.location.y = y
        }
    }

    fun pushOut(other: HitBox) {
        val dx1 = ((x + width) - other.x)
        val dx2 = ((other.x + other.width) - x)

        val dy1 = ((y + height) - other.y)
        val dy2 = ((other.y + other.height) - y)

        val minDx = min(dx1, dx2)
        val minDy = min(dy1, dy2)

        if (minDx < minDy) {
            if (entity.location.direction === Direction.RIGHT) {
                entity.dx = 0F
                x -= dx1
            } else {
                entity.dx = 0F
                x += dx2
            }
        } else {
            if (dy1 < dy2) {
                y -= dy1
                entity.dy = 0F
                entity.location.y = y
            } else if (dy2 < dy1) {
                y += dy2
                entity.dy = 0F
                entity.location.y = y
            }
        }
    }
}