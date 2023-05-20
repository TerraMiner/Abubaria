package d2t.terra.abubaria.hitbox

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.tileSizeF
import d2t.terra.abubaria.world.block.Block

class BlockHitBox(private val block: Block) :
    HitBox(
        block.x * tileSizeF,
        block.y * tileSizeF + tileSizeF * block.type.state.offset,
        tileSizeF - 1F,
        block.type.height - 1F
    ) {
    val clone get() = BlockHitBox(block)
}