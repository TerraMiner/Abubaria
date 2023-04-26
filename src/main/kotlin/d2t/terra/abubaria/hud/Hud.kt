package d2t.terra.abubaria.hud

import d2t.terra.abubaria.GamePanel.screenWidth2
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.entity.player.inventory.slotSize
import readImage
import scaleImage
import java.awt.Graphics2D
import java.io.File
import javax.imageio.ImageIO

object Hud {
    var healthBar = ""
    var inventory = ClientPlayer.inventory

    val path = "hud/inventory/"
    val selectedSlot = readImage("${path}selectedSlot.png")
    val hoveredSlot = readImage("${path}hoveredSlot.png")
    val slot = readImage("${path}slot.png")

    fun draw(g2: Graphics2D) {

        healthBar = ClientPlayer.run { "$health / $maxHealth" }

        g2.drawString(healthBar,screenWidth2 - healthBar.length * 8, 15)
        inventory.draw(g2)


    }
}