package d2t.terra.abubaria.hitbox

import d2t.terra.abubaria.location.Location

open class HitBox(var x: Double, var y: Double, var width: Double, var height: Double) {
    val bottom get() = y + height
    val top get() = y
    val right get() = x + width
    val left get() = x

    constructor(location: Location, sizeX: Double, sizeY: Double) : this(
        location.x,
        location.y,
        sizeX,
        sizeY
    )

    constructor(x: Int, y: Int, width: Int, height: Int) : this(
        x.toDouble(),
        y.toDouble(),
        width.toDouble(),
        height.toDouble()
    )

    fun setCoords(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    fun setLocation(location: Location) {
        location.also {
            x = it.x
            y = it.y
        }
    }

    open fun move(dx: Double, dy: Double): HitBox {
        x += dx
        y += dy
        return this
    }

    fun transform(x: Double, y: Double, width: Double, height: Double): HitBox {
        this.x += x
        this.y += y
        this.width += width
        this.height += height
        return this
    }

    fun intersects(other: HitBox) =
        (x < other.x + other.width && x + width > other.x)
                &&
                (y < other.y + other.height && y + height > other.y)


    fun touches(other: HitBox) =
        (x + width + 1 >= other.x && x + width <= other.x + 1 || x + 1 <= other.x + other.width && x >= other.x + other.width - 1)
                &&
                (y + height + 1 >= other.y && y + height <= other.y + 1 || y + 1 <= other.y + other.height && y >= other.y + other.height - 1)

}