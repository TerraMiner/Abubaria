package d2t.terra.abubaria.location

import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.GamePanel.world
import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.world.Block
import d2t.terra.abubaria.world.Chunk
import kotlin.math.min


class EntityHitBox(val entity: Entity, width: Double,height: Double) :
    HitBox(entity.location, width, height) {

    val clone
        get() = EntityHitBox(entity, width, height).also {
            it.x = x
            it.y = y
        }

    override fun move(dx: Double, dy: Double): EntityHitBox {
        x += dx
        y += dy
        return this
    }

    fun intersectionChunks(): MutableList<Chunk> {
        val chunks = mutableListOf<Chunk>()

        val leftPos = ((left + entity.dx) / tileSize).toInt()
        val rightPos = ((right + entity.dx) / tileSize).toInt()

        val topPos = ((top + entity.dy) / tileSize).toInt()
        val bottomPos = ((bottom + entity.dy) / tileSize).toInt()

        val leftTopChunk = world.getChunkAt(leftPos, topPos)
        val leftBottomChunk = world.getChunkAt(leftPos, bottomPos)

        val rightTopChunk = world.getChunkAt(rightPos, topPos)
        val rightBottomChunk = world.getChunkAt(rightPos, bottomPos)

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
            entity.dx = .0
            entity.location.x = x
        } else if (x + width > other.x + other.width) {
            x = other.x + other.width - width
            entity.dx = .0
            entity.location.x = x
        }

        if (y < other.y) {
            y = other.y
            entity.dy = .0
            entity.location.y = y
        } else if (y + height > other.y + other.height) {
            y = other.y + other.height - height
            entity.dy = .0
            entity.location.y = y
            isOnWorldBorder = true
        }
        entity.onWorldBorder = isOnWorldBorder
    }

    fun pushOutX(other: HitBox) {
        val dx1 = ((x + width) - other.x)
        val dx2 = ((other.x + other.width) - x)

        if (entity.location.direction === Direction.RIGHT) {
            entity.dx = .0
            x -= dx1
        } else {
            entity.dx = .0
            x += dx2
        }

    }

    fun pushOutY(other: HitBox) {
        val dy1 = ((y + height) - other.y)
        val dy2 = ((other.y + other.height) - y)

        if (dy1 < dy2) {
            y -= dy1
            entity.dy = .0
            entity.location.y = y
        } else if (dy2 < dy1) {
            y += dy2
            entity.dy = .0
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
                entity.dx = .0
                x -= dx1
            } else {
                entity.dx = .0
                x += dx2
            }
        } else {
            if (dy1 < dy2) {
                y -= dy1
                entity.dy = .0
                entity.location.y = y
            } else if (dy2 < dy1) {
                y += dy2
                entity.dy = .0
                entity.location.y = y
            }
        }
    }
}

class BlockHitBox(private val block: Block) :
    HitBox(
        block.x * tileSize,
        block.y * tileSize + (tileSize * block.type.state.offset).toInt(),
        tileSize - 1,
        block.type.height - 1
    ) {
    val clone get() = BlockHitBox(block)
}


open class HitBox(var x: Double, var y: Double, var width: Double, var height: Double) {
    val bottom get() = y + height
    val top get() = y
    val right get() = x + width
    val left get() = x

    constructor(location: Location, sizeX: Double, sizeY: Double) : this(
        location.x,
        location.y,
        sizeX,
        sizeY
    )

    constructor(x: Int, y: Int, width: Int, height: Int) : this(
        x.toDouble(),
        y.toDouble(),
        width.toDouble(),
        height.toDouble()
    )

    fun setCoords(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    fun setLocation(location: Location) {
        location.also {
            x = it.x
            y = it.y
        }
    }

    open fun move(dx: Double, dy: Double): HitBox {
        x += dx
        y += dy
        return this
    }

    fun transform(x: Double,y: Double,width: Double,height: Double): HitBox {
        this.x += x
        this.y += y
        this.width += width
        this.height += height
        return this
    }

    fun intersects(other: HitBox) =
        (x < other.x + other.width && x + width > other.x)
        &&
        (y < other.y + other.height && y + height > other.y)


    fun touches(other: HitBox) =
        (x + width + 1 >= other.x && x + width <= other.x + 1 || x + 1 <= other.x + other.width && x >= other.x + other.width - 1)
        &&
        (y + height + 1 >= other.y && y + height <= other.y + 1 || y + 1 <= other.y + other.height && y >= other.y + other.height - 1)

}