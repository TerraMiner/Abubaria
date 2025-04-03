package d2t.terra.abubaria.entity

import d2t.terra.abubaria.interpBound
import d2t.terra.abubaria.location.Location
import org.joml.Math
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.io.graphics.window.FpsLimit
import kotlin.math.abs

interface MovingObject {
    val renderLocation: Location

    val baseInterpSpeed: Float get() = .1f

    fun interpSpeed(speed: Float = baseInterpSpeed): Float {
        return speed * FpsLimit.FPS_240.value / Window.fpsLimit
    }

    fun updateRenderLocation(location: Location, speed: Float = baseInterpSpeed) {
        renderLocation.x = moveTowards(renderLocation.x,location.x,speed)
        renderLocation.y = moveTowards(renderLocation.y,location.y,speed)
    }

    fun moveTowards(from: Float, to: Float, speed: Float): Float {
        val d = abs(from - to)
        val safeSpeed = interpSpeed(speed)
        val speedX = minOf(safeSpeed, if (d < interpBound) 0f else 1f)
        return if (d < interpBound) to
        else lerp(from, to, speedX)
    }

    fun lerp(a: Float, b: Float, t: Float): Float {
        val result = a + (b - a) * t
        return if ((a < b && result > b) || (a > b && result < b)) b else result
    }
}