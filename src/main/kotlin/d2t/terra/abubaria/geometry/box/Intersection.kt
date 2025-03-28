package d2t.terra.abubaria.geometry.box

import d2t.terra.abubaria.world.block.BlockFace
import org.joml.Vector2f
import kotlin.math.abs

class Intersection(
    val vecFrom: Vector2f,
    val vecDir: Vector2f,
    val distance: Float,
    val tMin: Float,
    val tMax: Float,
    val parent: CollisionBox
) {

    val exists by lazy { tMax >= tMin && tMax >= 0 && (distance < 0 || tMin <= distance) }

    val nearestIntersection by lazy {
        if (exists) Vector2f(
            vecFrom.x + tMin * vecDir.x,
            vecFrom.y + tMin * vecDir.y,
        ) else null
    }

    val furtherIntersection by lazy {
        if (exists) Vector2f(
            vecFrom.x + tMax * vecDir.x,
            vecFrom.y + tMax * vecDir.y,
        ) else null
    }

    val nearestFace by lazy {
        if (!exists) return@lazy null
        val intersectionPoint = nearestIntersection ?: return@lazy null

        val x = intersectionPoint.x
        val y = intersectionPoint.y

        val epsilon = 0.00001

        when {
            abs(parent.x - x) < epsilon -> BlockFace.LEFT
            abs(parent.y - y) < epsilon -> BlockFace.BOTTOM
            abs(parent.x - x + parent.sizeX) < epsilon -> BlockFace.RIGHT
            abs(parent.y - y + parent.sizeY) < epsilon -> BlockFace.TOP
            else -> null
        }
    }
}