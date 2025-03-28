package d2t.terra.abubaria.geometry

import d2t.terra.abubaria.tileSizeF
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.block.Position
import kotlin.math.floor

val Location.position get() = Position(floor(x / tileSizeF).toInt(), floor(y / tileSizeF).toInt())