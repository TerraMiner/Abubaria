package d2t.terra.abubaria.entity

import d2t.terra.abubaria.entity.type.EntityType
import d2t.terra.abubaria.location.Direction
import d2t.terra.abubaria.location.Location

abstract class LivingEntity(type: EntityType, location: Location) : PhysicalEntity(type,location) {
    var health: Float = 0f
    var maxHealth: Float = 0f

    fun moveLeft() {
        oldLocation.direction = Direction.LEFT
        movement.x -= speed
    }

    fun moveRight() {
        oldLocation.direction = Direction.RIGHT
        movement.x += speed
    }
}