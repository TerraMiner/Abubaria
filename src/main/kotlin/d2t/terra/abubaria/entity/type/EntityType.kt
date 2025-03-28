package d2t.terra.abubaria.entity.type

import d2t.terra.abubaria.entity.AbstractEntity
import d2t.terra.abubaria.entity.impl.ItemEntity
import d2t.terra.abubaria.entity.impl.ClientPlayer

enum class EntityType(
    val width: Float,
    val height: Float,
    val clazz: Class<out AbstractEntity>
) {
    PLAYER(22.4f, 46.4f, ClientPlayer::class.java),
    ITEM(24.0f, 24.0f, ItemEntity::class.java)
}