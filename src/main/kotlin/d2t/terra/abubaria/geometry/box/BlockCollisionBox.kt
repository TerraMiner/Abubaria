package d2t.terra.abubaria.geometry.box

import d2t.terra.abubaria.tileSizeF
import d2t.terra.abubaria.world.block.Block
import d2t.terra.abubaria.world.block.BlockFace
import d2t.terra.abubaria.world.material.Material

class BlockCollisionBox(
    val block: Block,
    type: Material
) : CollisionBox(
    block.x * tileSizeF,
    block.y * tileSizeF + tileSizeF * type.state.offset,
    tileSizeF,
    tileSizeF * type.scale
) {
    constructor(block: Block) : this(block,block.type)

    override val boxType = CollisionBoxType.BLOCK

    fun relativeBox(face: BlockFace, dist: Int): CollisionBox? {
        return block.relative(face, dist)?.collisionBox
    }

    override fun equals(other: Any?): Boolean {
        return hashCode() == other.hashCode()
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + block.hashCode()
        return result
    }

    override fun clone(): BlockCollisionBox {
        return super.clone() as BlockCollisionBox
    }
}