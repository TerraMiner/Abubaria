package d2t.terra.abubaria.hitbox

import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.io.graphics.drawRect
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.material.MaterialSize

open class HitBox(var x: Float, var y: Float, var width: Float, var height: Float) {
    val bottom get() = y + height
    val top get() = y
    val right get() = x + width
    val left get() = x

    constructor(location: Location, sizeX: Float, sizeY: Float) : this(
        location.x,
        location.y,
        sizeX,
        sizeY
    )

    constructor(x: Int, y: Int, width: Int, height: Int) : this(
        x.toFloat(),
        y.toFloat(),
        width.toFloat(),
        height.toFloat()
    )

    fun setCoords(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun setLocation(location: Location) {
        location.also {
            x = it.x
            y = it.y
        }
    }

    open fun move(dx: Float, dy: Float): HitBox {
        x += dx
        y += dy
        return this
    }

    fun transform(x: Float, y: Float, width: Float, height: Float): HitBox {
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

    fun draw(playerLoc: Location) {
        val screenX = Camera.worldScreenPosX((x).toInt(), playerLoc)
        val screenY = Camera.worldScreenPosY((y).toInt(), playerLoc)
        drawRect(screenX,screenY,width,height)
    }

}