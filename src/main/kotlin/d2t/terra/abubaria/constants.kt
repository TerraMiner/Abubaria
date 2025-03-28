package d2t.terra.abubaria

const val tickrate = 256

const val originalTileSize = 8
private const val scale = 2

const val tileSize = originalTileSize * scale
const val tileSizeF = originalTileSize.toFloat() * scale.toFloat()

const val chunkSize = 8
const val chunkBitMask = 3
const val blockBitMask = chunkSize - 1
const val lCount = 4
const val lChunkBitMask = 2
const val lightBitMask = lCount - 1
const val lSize = tileSize / 4
const val lSizeF = tileSizeF / 4
const val lightLevels = 16
const val particleSize = 4
const val slotSize = 42F
const val inSlotPos = 10
const val diff = 20F
const val entityItemSize = 12