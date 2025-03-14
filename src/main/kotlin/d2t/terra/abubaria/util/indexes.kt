package d2t.terra.abubaria.util

import d2t.terra.abubaria.world.block.Position

fun getIndex(x: Int, y: Int, maxX: Int, maxY: Int): Int {
    return x.coerceIn(0,maxX-1) + y.coerceIn(0,maxY-1) * maxX
}

fun getIndex(pos: Position, maxX: Int, maxY: Int): Int {
    return getIndex(pos.x,pos.y,maxX,maxY)
}

fun getCoords(index: Int, maxX: Int, maxY: Int): Position {
    return Position((index % maxX).coerceIn(0, maxX - 1), (index / maxX).coerceIn(0, maxY - 1))
}