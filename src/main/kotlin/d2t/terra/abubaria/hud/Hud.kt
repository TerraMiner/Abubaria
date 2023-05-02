package d2t.terra.abubaria.hud

import d2t.terra.abubaria.GamePanel.screenWidth2
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.entity.player.inventory.slotSize
import lwjgl.drawString
import lwjgl.loadImage
import readImage
import scaleImage
import java.awt.Graphics2D
import java.io.File
import javax.imageio.ImageIO

object Hud {
    var healthBar = ""
    var inventory = ClientPlayer.inventory

    val path = "hud/inventory/"
    val selectedSlot = /*readImage(*/loadImage("${path}selectedSlot.png")/*)*/
    val hoveredSlot = /*readImage(*/loadImage("${path}hoveredSlot.png")/*)*/
    val slot = /*readImage(*/loadImage("${path}slot.png")/*)*/

    fun draw() {

        healthBar = ClientPlayer.run { "$health / $maxHealth" }

        drawString(healthBar,screenWidth2 - healthBar.length * 8, 15,4)
        inventory.draw()


    }
}