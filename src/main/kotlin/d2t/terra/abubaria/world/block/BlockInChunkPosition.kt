package d2t.terra.abubaria.world.block

@JvmInline
value class BlockInChunkPosition(val value: Byte) {
    val x get() = (value.toInt() ushr 3) and 0x7
    val y get() = value.toInt() and 0x7

    companion object {
        fun decode(x: Int, y: Int): BlockInChunkPosition {
            return BlockInChunkPosition((((x and 0x7) shl 3) or (y and 0x7)).toByte())
        }
    }
}