package d2t.terra.abubaria.light

import d2t.terra.abubaria.world.block.BlockInChunkPosition

@JvmInline
value class LightInBlockPosition(val value: Byte) {
    val x get() = (value.toInt() shr 2) and 0x3
    val y get() = value.toInt() and 0x3

    companion object {
        fun decode(x: Int, y: Int): BlockInChunkPosition {
            return BlockInChunkPosition((((x and 0x3) shl 2) or (y and 0x3)).toByte())
        }
    }
}