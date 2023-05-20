import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.Entity
import d2t.terra.abubaria.hitbox.HitBox
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.block.Block

object CollisionHandler {

    fun Entity.checkIfStuck(hitBox: HitBox): Boolean {
        chunks.forEach chunks@{ chunk ->
            chunk.blockMap.forEach blockCols@{ blockCols ->
                blockCols.forEach blocks@{ block ->
                    if (block.hitBox.clone.transform(hitBox.width/2, hitBox.height/2, -(hitBox.width/2), -(hitBox.height/2)).intersects(hitBox) && block.type.collideable) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun Entity.checkCollision() {
        chunks.forEach chunks@{ chunk ->
            chunk.blockMap.forEach blockCols@{ blockCols ->
                blockCols.forEach blocks@{ block ->

                    if (hitBox.clone.move(dx, dy)
                            .intersects(block.hitBox) && block.type.collideable) {
                        climb(block)
                    }

                    //Horizontal
                    if (hitBox.clone.move(dx, 0F).intersects(block.hitBox) && block.type.collideable) {
                        hitBox.pushOutX(block.hitBox)
                    }

                    //Vertical
                    if (hitBox.clone.move(0F, dy).intersects(block.hitBox) && block.type.collideable) {
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

            if (futureBox.intersectionChunks().any { it.blockMap.flatten().any { b -> b != block && b.type.collideable && b.hitBox.intersects(futureBox) } }) return

            location.x = futureBox.x
            location.y = futureBox.y

            hitBox.setLocation(location)
        }
    }
}