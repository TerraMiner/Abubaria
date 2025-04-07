package d2t.terra.abubaria.entity.type

import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.entity.impl.item.ItemEntity
import d2t.terra.abubaria.entity.impl.ClientPlayer

enum class EntityType(
    val width: Float,
    val height: Float,
    val clazz: Class<out Entity>
) {
    PLAYER(22f, 46f, ClientPlayer::class.java),
    ITEM(12.0f, 12.0f, ItemEntity::class.java)
}