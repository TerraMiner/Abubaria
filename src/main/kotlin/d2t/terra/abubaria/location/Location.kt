package d2t.terra.abubaria.location

import kotlin.math.pow
import kotlin.math.sqrt

open class Location(var x: Float = 0f, var y: Float = 0f, var direction: Direction = Direction.LEFT) {
    constructor(x: Int, y: Int) : this(x.toFloat(),y.toFloat(),Direction.LEFT)
    constructor(x: Int, y: Int, direction: Direction) : this(x.toFloat(),y.toFloat(),direction)

    val clone: Location get() = Location(x,y,direction)

    fun setLocation(location: Location) {
        x = location.x
        y = location.y
        direction = location.direction
    }

    fun setLocation(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun move(dx: Float, dy: Float): Location {
        x += dx; y += dy
        return this
    }

    fun transfer(dx: Float, dy: Float): Location {
        return clone.also {
            it.x += dx; it.y += dy
        }
    }

    fun distance(other: Location): Float {
        val dx = x - other.x
        val dy = y - other.y
        return sqrt(dx.pow(2) + dy.pow(2))
    }
}
