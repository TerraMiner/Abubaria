package d2t.terra.abubaria.geometry

import d2t.terra.abubaria.location.Location
import org.joml.Vector2f
import kotlin.math.abs

fun Location.toVector2f() = Vector2f(x,y)

fun Vector2f.subtract(vec: Vector2f) = subtract(vec.x, vec.y)

fun Vector2f.subtract(vec: Location) = subtract(vec.x, vec.y)

fun Vector2f.subtract(dx: Float, dy: Float): Vector2f {
    x -= dx
    y -= dy
    return this
}

val Vector2f.isZero get() = x == 0f && y == 0f

val Vector2f.isNan get() = x.isNaN() || y.isNaN()

fun Vector2f.lessThan(value: Float) =
    abs(x) < value.toFloat() && abs(y) < value.toFloat()

fun Vector2f.setX(x: Float) = apply { this.x = x.toFloat() }

fun Vector2f.setY(y: Float) = apply { this.y = y.toFloat() }

val Vector2f.clone get() = Vector2f(x, y)

operator fun Vector2f.times(value: Float): Vector2f = mul(value.toFloat())

operator fun Vector2f.plus(v2: Vector2f): Vector2f = add(v2)

operator fun Vector2f.minus(v2: Vector2f): Vector2f = subtract(v2)

operator fun Vector2f.unaryMinus(): Vector2f = mul(-1F)

fun Vector2f.reflectVector(surfaceNormal: Vector2f): Vector2f {
    val dotProduct = dot(surfaceNormal)
    return this - surfaceNormal * (2 * dotProduct)
}