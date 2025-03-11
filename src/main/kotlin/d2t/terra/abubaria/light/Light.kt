package d2t.terra.abubaria.light

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.world.block.Position
import d2t.terra.abubaria.world.lChunkBitMask
import d2t.terra.abubaria.world.lightLevels
import d2t.terra.abubaria.world.material.Material
import vbotests.util.loopWhile

class Light(
    val lightPos: LightInBlockPosition,
    val pos: Position?,
    var power: Int = 1,
) {

    val world get() = GamePanel.world
    val block get() = pos?.let { world.getBlockAt(it) }

    fun initializePower() {
        pos ?: run {
            power = 0
            return
        }

        if (block?.type === Material.AIR) {
            power = 1
            return
        }

        power = findDistanceToAir()

    }

    private fun findDistanceToAir(): Int {
        pos ?: return 0

        val block = block!!
        val offsetX = (block.x shl lChunkBitMask) + lightPos.x
        val offsetY = (block.y shl lChunkBitMask) + lightPos.y

        loopWhile(1, lightLevels) { r ->
            loopWhile(0, r) { x ->
                val y = r - x
                val px1 = (x + offsetX) shr lChunkBitMask
                val py1 = (y + offsetY) shr lChunkBitMask

                val px2 = (-x + offsetX) shr lChunkBitMask
                val py2 = (y + offsetY) shr lChunkBitMask

                val px3 = (x + offsetX) shr lChunkBitMask
                val py3 = (-y + offsetY) shr lChunkBitMask

                val px4 = (-x + offsetX) shr lChunkBitMask
                val py4 = (-y + offsetY) shr lChunkBitMask

                if (
                    world.getBlockAt(px1, py1)?.type === Material.AIR ||
                    world.getBlockAt(px2, py2)?.type === Material.AIR ||
                    world.getBlockAt(px3, py3)?.type === Material.AIR ||
                    world.getBlockAt(px4, py4)?.type === Material.AIR
                ) return r
            }
        }

        return lightLevels
    }
}




