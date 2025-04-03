package d2t.terra.abubaria.location

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.geometry.box.CollisionBox
import d2t.terra.abubaria.world.World
import org.joml.Vector2f
import kotlin.math.pow
import kotlin.math.sqrt

open class Location(
    var x: Float = 0f,
    var y: Float = 0f,
    var direction: Direction = Direction.LEFT,
    val world: World = GamePanel.world
) {
    constructor(x: Int, y: Int) : this(x.toFloat(), y.toFloat(), Direction.LEFT)
    constructor(x: Int, y: Int, direction: Direction) : this(x.toFloat(), y.toFloat(), direction)

    val clone: Location get() = Location(x, y, direction, world)

    fun set(location: Location): Location {
        x = location.x
        y = location.y
        direction = location.direction
        return this
    }

    fun set(collisionBox: CollisionBox): Location {
        x = collisionBox.x
        y = collisionBox.y
        return this
    }

    fun set(x: Float, y: Float): Location {
        this.x = x
        this.y = y
        return this
    }

    fun move(dx: Float, dy: Float): Location {
        x += dx; y += dy
        return this
    }

    fun move(vec: Vector2f): Location = move(vec.x, vec.y)

    fun transfer(dx: Float, dy: Float): Location {
        return clone.move(dx, dy)
    }

    fun distance(other: Location): Float {
        val dx = x - other.x
        val dy = y - other.y
        return sqrt(dx.pow(2) + dy.pow(2))
    }

    override fun toString(): String {
        return "Location: [$x, $y, $direction]"
    }
}
