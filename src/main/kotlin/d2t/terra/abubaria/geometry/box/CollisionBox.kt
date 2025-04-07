package d2t.terra.abubaria.geometry.box

import d2t.terra.abubaria.blockChunkShiftBits
import d2t.terra.abubaria.blockShiftBits
import d2t.terra.abubaria.chunkShiftBits
import d2t.terra.abubaria.tileSizeF
import org.joml.Vector2f
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.util.for2d
import d2t.terra.abubaria.util.square
import d2t.terra.abubaria.world.Chunk
import d2t.terra.abubaria.world.World
import d2t.terra.abubaria.world.block.Position
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

open class CollisionBox(
    var x: Float,
    var y: Float,
    sizeX: Float,
    sizeY: Float,
) : Cloneable {

    companion object {
        private val EMPTY_CHUNK_LIST = listOf<Chunk>()

        fun of(minPoint: Position, maxPoint: Position): CollisionBox {
            val sizeX = maxPoint.x - minPoint.x
            val sizeY = maxPoint.y - minPoint.y

            return CollisionBox(minPoint.x.toFloat(), minPoint.y.toFloat(), sizeX.toFloat(), sizeY.toFloat())
        }

        fun of(minPoint: Location, maxPoint: Location): CollisionBox {
            val sizeX = maxPoint.x - minPoint.x
            val sizeY = maxPoint.y - minPoint.y

            return CollisionBox(minPoint.x, minPoint.y, sizeX, sizeY)
        }
    }

    open val boxType = CollisionBoxType.DEFAULT
    var colliderType = ColliderType.OUTSIDE

    var sizeX = sizeX
        set(value) {
            if (field == value) return
            field = value
            updateBoxSize()
        }

    var sizeY = sizeY
        set(value) {
            if (field == value) return
            field = value
            updateBoxSize()
        }

    val maxX get() = x + sizeX
    val maxY get() = y + sizeY

    val centerX get() = x + sizeX / 2F
    val centerY get() = y + sizeY / 2F

    var _boxSize: Float? = null
    val boxSize: Float
        get() = _boxSize ?: sqrt(square(sizeX / 2) + square(sizeY / 2)).also {
            _boxSize = it
        }

    var offset = Vector2f(.5F, 0F)

    public override fun clone(): CollisionBox {
        return super.clone() as CollisionBox
    }

    fun diff(x: Float = 0F, y: Float = 0F) = clone().move(x, y)

    fun diff(vec: Vector2f) = diff(vec.x, vec.y)

    fun isType(type: CollisionBoxType) = boxType === type

    fun move(x: Float = 0F, y: Float = 0F): CollisionBox {
        this.x += x
        this.y += y
        return this
    }

    fun move(vec: Vector2f) {
        move(vec.x, vec.y)
    }

    fun expandX(delta: Float): CollisionBox {
        val value = delta
        if (value < 0) x += value
        sizeX += abs(value)
        return this
    }

    fun expandY(delta: Float): CollisionBox {
        val value = delta
        if (value < 0) y += value
        sizeY += abs(value)
        return this
    }

    fun expand(dX: Float = 0F, dY: Float = 0F): CollisionBox {
        expandX(dX);expandY(dY)
        return this
    }

    fun expand(vec: Vector2f): CollisionBox {
        expand(vec.x, vec.y)
        return this
    }

    fun extendX(delta: Float): CollisionBox {
        expandX(delta); expandX(-delta)
        return this
    }

    fun extendY(delta: Float): CollisionBox {
        expandY(delta); expandY(-delta)
        return this
    }

    fun extend(dX: Float = 0F, dY: Float = 0F): CollisionBox {
        extendX(dX);extendY(dY)
        return this
    }

    fun extend(vec: Vector2f): CollisionBox {
        extend(vec.x, vec.y)
        return this
    }

    fun teleport(location: Location): CollisionBox {
        teleport(
            location.x, //- sizeX * offset.x,
            location.y //- sizeY * offset.y
        )
        return this
    }

    fun teleport(collisionBox: CollisionBox): CollisionBox {
        x = collisionBox.x
        y = collisionBox.y
        return this
    }

    fun teleport(x: Float = this.x, y: Float = this.y): CollisionBox {
        this.x = x
        this.y = y
        return this
    }

    fun intersectsX(other: CollisionBox) = maxX > other.x && x < other.maxX

    fun intersectsY(other: CollisionBox) = maxY > other.y && y < other.maxY

    fun outtersectsX(other: CollisionBox) = x <= other.x || maxX >= other.maxX

    fun outtersectsY(other: CollisionBox) = y <= other.y || maxY >= other.maxY

    fun intersects(other: CollisionBox) = intersectsX(other) && intersectsY(other)

    fun outtersects(other: CollisionBox) = outtersectsX(other) || outtersectsY(other)

    fun touchX(other: CollisionBox) = maxX >= other.x && x <= other.maxX

    fun touchY(other: CollisionBox) = maxY >= other.y && y <= other.maxY

    fun touch(other: CollisionBox) = touchX(other) && touchY(other)

    fun intersection(vecFrom: Vector2f, vecDir: Vector2f, rayDistance: Float = -1F): Intersection {
        val tMinX = (x - vecFrom.x) / vecDir.x
        val tMaxX = (maxX - vecFrom.x) / vecDir.x

        val tMinY = (y - vecFrom.y) / vecDir.y
        val tMaxY = (maxY - vecFrom.y) / vecDir.y

        val tMin = maxOf(maxOf(minOf(tMinX, tMaxX), minOf(tMinY, tMaxY)))
        val tMax = minOf(minOf(maxOf(tMinX, tMaxX), maxOf(tMinY, tMaxY)))
        val distance = rayDistance
        return Intersection(vecFrom, vecDir, distance, tMin, tMax, this)
    }

    fun collideX(other: CollisionBox, offset: Float): Float {
        if (!other.intersectsY(this)) return offset
        return if (colliderType === ColliderType.OUTSIDE)
            collideAxis(offset, x, maxX, other.x, other.maxX)
        else recollideAxis(offset, x, maxX, other.x, other.maxX)
    }

    fun collideY(other: CollisionBox, offset: Float): Float {
        if (!other.intersectsX(this)) return offset
        return if (colliderType === ColliderType.OUTSIDE)
            collideAxis(offset, y, maxY, other.y, other.maxY)
        else recollideAxis(offset, y, maxY, other.y, other.maxY)
    }

    private fun collideAxis(offset: Float, axis: Float, axisMax: Float, oAxis: Float, oAxisMax: Float): Float {
        return if (offset > 0F && oAxisMax <= axis) {
            val dz = axis - oAxisMax
            if (dz < offset) dz else offset
        } else if (offset < 0f && oAxis >= axisMax) {
            val dz = axisMax - oAxis
            if (dz > offset) dz else offset
        } else offset
    }

    private fun recollideAxis(offset: Float, axis: Float, axisMax: Float, oAxis: Float, oAxisMax: Float): Float {
        return if (offset > 0F && oAxisMax <= axisMax) {
            val dz = axisMax - oAxisMax
            if (dz < offset) dz else offset
        } else if (offset < 0f && oAxis >= axis) {
            val dz = axis - oAxis
            if (dz > offset) dz else offset
        } else offset
    }

    fun getCollidingBlocks(
        world: World,
        list: MutableList<CollisionBox> = mutableListOf(),
        onlyCollideable: Boolean = true
    ): MutableList<CollisionBox> {
        val startX = floor(x).toInt() shr blockShiftBits
        val endX = ceil(x + sizeX).toInt() shr blockShiftBits
        val startY = floor(y).toInt() shr blockShiftBits
        val endY = ceil(y + sizeY).toInt() shr blockShiftBits
        return list.apply {
            for2d(startX, endX, startY, endY) { x, y ->
                takeBoxes(x, y, world, onlyCollideable)
            }
        }
    }

    fun getCollidingChunks(world: World): List<Chunk> {
        val startX = floor(x).toInt() shr blockChunkShiftBits
        val endX = ceil(x + sizeX).toInt() shr blockChunkShiftBits
        val startY = floor(y).toInt() shr blockChunkShiftBits
        val endY = ceil(y + sizeY).toInt() shr blockChunkShiftBits
        var list: MutableList<Chunk>? = null
        for2d(startX, endX, startY, endY) { x, y ->
            world.getChunk(x, y)?.let {
                if (list == null) list = mutableListOf<Chunk>()
                list?.add(it)
            }
        }
        return list ?: EMPTY_CHUNK_LIST
    }

    private fun MutableList<CollisionBox>.takeBoxes(
        x: Int,
        y: Int,
        world: World,
        onlyCollideable: Boolean = true
    ) {
        val block = world.getBlockAt(x, y) ?: return
        if (onlyCollideable && block.type.isCollideable) add(block.collisionBox)
    }

    fun distanceX(o: CollisionBox) = square(centerX - o.centerX)

    fun distanceY(o: CollisionBox) = square(centerY - o.centerY)

    fun distance(o: CollisionBox) = sqrt(distanceX(o) + distanceY(o))

    fun sidedDistance(o: CollisionBox): Float {
        if (intersects(o)) return 0f

        val dx = minDist(x, o.x, maxX, o.maxX)
        val dy = minDist(y, o.y, maxY, o.maxY)

        return sqrt(dx * dx + dy * dy)
    }

    private fun minDist(axis: Float, oAxis: Float, axisMax: Float, oAxisMax: Float): Float {
        return when {
            axisMax < oAxis -> oAxis - axisMax
            oAxisMax < axis -> axis - oAxisMax
            else -> 0f
        }
    }

    private fun updateBoxSize() {
        if (_boxSize === null) return
        _boxSize = sqrt(square(sizeX / 2) + square(sizeY / 2))
    }

    override fun toString(): String {
        return "${javaClass.simpleName}:[$x, $y, $sizeX, $sizeY]"
    }

    override fun equals(other: Any?): Boolean {
        return (other is CollisionBox && other.hashCode() == hashCode())
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + sizeX.hashCode()
        result = 31 * result + sizeY.hashCode()
        return result
    }
}