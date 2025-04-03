package d2t.terra.abubaria.world.block

import d2t.terra.abubaria.chunkShiftBits

class Position(var x: Int, var y: Int) {
    val inChunkPosition get() = BlockInChunkPosition.decode(x,y)
    val chunkPosition get() = Position(chunkX,chunkY)
    val chunkX get() = x shr chunkShiftBits
    val chunkY get() = y shr chunkShiftBits



    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Position) return false

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }
}