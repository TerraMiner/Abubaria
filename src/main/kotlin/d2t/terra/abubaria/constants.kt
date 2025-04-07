package d2t.terra.abubaria

const val tickrate = 256

const val tileSize = 16
const val tileSizeF = 16f

const val chunkSize = 8
const val chunkShiftBits = 3
const val blockShiftBits = 4
const val blockChunkShiftBits = blockShiftBits + chunkShiftBits
const val blockIndexMask = chunkSize - 1
const val particleSize = 4
const val slotSize = 42F
const val inSlotPos = 10
const val diff = 20F