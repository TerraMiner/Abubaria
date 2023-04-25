import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.location.HitBox
import d2t.terra.abubaria.world.Block
import d2t.terra.abubaria.world.BlockFace

object CollisionHandler {

    fun Entity.checkIfStuck(hitBox: HitBox): Boolean {
        chunks.forEach chunks@{ chunk ->
            chunk.blocks.forEach blockCols@{ blockCols ->
                blockCols.forEach blocks@{ block ->
                    if (hitBox.intersects(block.hitBox) && block.material.collideable) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun Entity.checkCollision() {
        chunks.forEach chunks@{ chunk ->
            chunk.blocks.forEach blockCols@{ blockCols ->
                blockCols.forEach blocks@{ block ->

                    //Horizontal

                    if (hitBox.clone.apply { move(dx, dy) }
                            .intersects(block.hitBox) && block.material.collideable) {
                        climb(block)
                    }

                    if (hitBox.clone.apply { move(dx, .0) }
                            .intersects(block.hitBox) && block.material.collideable) {
                        hitBox.pushOutX(block.hitBox)
                    }

                    //Vertical
                    if (hitBox.clone.apply { move(.0, dy) }
                            .intersects(block.hitBox) && block.material.collideable) {
                        hitBox.pushOutY(block.hitBox)
                    }

                }
            }
        }
    }


    private fun Entity.climb(block: Block) {
        if (hitBox.bottom - block.hitBox.top <= hitBox.height / hitBox.entity.height && autoClimb && (onGround || onWorldBorder)) {

            val futureBox = hitBox.clone
            futureBox.x += dx
            futureBox.y = block.hitBox.top - hitBox.height

            if (futureBox.intersectionChunks().any { it.blocks.flatten().any { b -> b != block && b.material.collideable && b.hitBox.intersects(futureBox) } }) return

            location.x = futureBox.x
            location.y = futureBox.y

            hitBox.setLocation(location)
        }
    }
}