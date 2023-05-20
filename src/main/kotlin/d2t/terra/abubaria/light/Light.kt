package d2t.terra.abubaria.light

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.world.block.Block
import d2t.terra.abubaria.world.lCount
import d2t.terra.abubaria.world.lightLevels
import d2t.terra.abubaria.world.material.Material

class Light(val x: Float, val y: Float, val inBlockX: Int, val inBlockY: Int, var power: Int, val block: Block? = null) {

    val world = GamePanel.world

    fun initializePower() {
        block ?: return

        if (block.type == Material.AIR) {
            power = 1
            return
        }

        power = findDistanceToAir()

    }

    private fun findDistanceToAir(): Int {
        block ?: return 0

        val offsetX = block.x * lCount + inBlockX
        val offsetY = block.y * lCount + inBlockY

        (1..lightLevels).forEach { r ->
            (0..r).forEach { x ->
                val y = r - x
                val points = mutableSetOf(
                    Pair(x + offsetX, y + offsetY), Pair(-x + offsetX, y + offsetY),
                    Pair(x + offsetX, -y + offsetY), Pair(-x + offsetX, -y + offsetY)
                )

                points.firstOrNull { (x, y) -> world.getBlockAt(x / lCount, y / lCount)?.type === Material.AIR }
                    ?.let { return r }
            }
        }

        return lightLevels
    }

}




