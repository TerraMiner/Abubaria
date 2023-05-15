package d2t.terra.abubaria.location

import kotlin.math.pow
import kotlin.math.sqrt

open class Location(var x: Double = .0, var y: Double = .0, var direction: Direction = Direction.LEFT) {
    constructor(x: Int, y: Int) : this(x.toDouble(),y.toDouble(),Direction.LEFT)
    constructor(x: Int, y: Int, direction: Direction) : this(x.toDouble(),y.toDouble(),direction)

    val clone: Location get() = Location(x,y,direction)

    fun setLocation(location: Location) {
        x = location.x
        y = location.y
        direction = location.direction
    }

    fun setLocation(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    fun move(dx: Double, dy: Double): Location {
        x += dx; y += dy
        return this
    }

    fun transfer(dx: Double, dy: Double): Location {
        return clone.also {
            it.x += dx; it.y += dy
        }
    }

    fun distance(other: Location): Double {
        val dx = x - other.x
        val dy = y - other.y
        return sqrt(dx.pow(2) + dy.pow(2))
    }
}
