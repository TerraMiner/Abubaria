package d2t.terra.abubaria.hitbox

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.world.block.Block

class BlockHitBox(private val block: Block) :
    HitBox(
        block.x * GamePanel.tileSize,
        block.y * GamePanel.tileSize + (GamePanel.tileSize * block.type.state.offset).toInt(),
        GamePanel.tileSize - 1,
        block.type.height - 1
    ) {
    val clone get() = BlockHitBox(block)
}