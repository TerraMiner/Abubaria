package d2t.terra.abubaria.geometry.box

import d2t.terra.abubaria.entity.PhysicalEntity

class EntityCollisionBox(
    val entity: PhysicalEntity,
) : CollisionBox(entity.location.x, entity.location.y, entity.type.width, entity.type.height) {

    override val boxType = CollisionBoxType.ENTITY

    override fun equals(other: Any?): Boolean {
        return hashCode() == other.hashCode()
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + entity.hashCode()
        return result
    }

    override fun clone(): EntityCollisionBox {
        return super.clone() as EntityCollisionBox
    }
}