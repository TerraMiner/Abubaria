package d2t.terra.abubaria.light

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.world.block.Block
import java.util.concurrent.ConcurrentLinkedQueue

object LightManager {

    val forUpDate = ConcurrentLinkedQueue<Block>()

    fun tick() {
        val tickInterval = 1e9 / 256.0
        var deltaTicks = .0
        var lastTime = System.nanoTime()
        var currentTime: Long
        var timer = 0L
        var tickCount = 0
        Window.whileWindowOpened {
            currentTime = System.nanoTime()
            deltaTicks += (currentTime - lastTime) / tickInterval
            timer += (currentTime - lastTime)
            lastTime = currentTime

            while (deltaTicks >= 1.0) {
                forUpDate.forEach { block ->
                    block.lightMap.forEach(Light::initializePower)
                    forUpDate.remove(block)
                }
                deltaTicks = .0
                ++tickCount
            }

            if (timer >= 1e9) {
                GamePanel.display.lps = tickCount
                tickCount = 0
                timer = 0
            }
        }
    }
}