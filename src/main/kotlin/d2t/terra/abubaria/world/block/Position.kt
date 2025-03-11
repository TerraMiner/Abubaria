package d2t.terra.abubaria.world.block

import d2t.terra.abubaria.world.blockBitMask

class Position(var x: Int, var y: Int) {
    val inChunkPosition get() = BlockInChunkPosition.decode(x,y)
    val chunkX get() = x and blockBitMask
    val chunkY get() = y and blockBitMask
}