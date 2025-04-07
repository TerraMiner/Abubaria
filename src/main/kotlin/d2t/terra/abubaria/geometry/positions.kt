package d2t.terra.abubaria.geometry

import d2t.terra.abubaria.blockShiftBits
import d2t.terra.abubaria.tileSizeF
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.block.Position
import kotlin.math.floor

val Location.position get() = Position(floor(x).toInt() shr blockShiftBits, floor(y).toInt() shr blockShiftBits)