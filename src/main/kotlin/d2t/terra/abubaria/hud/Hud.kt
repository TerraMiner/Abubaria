package d2t.terra.abubaria.hud

import d2t.terra.abubaria.GamePanel.defaultScreenWidth
import d2t.terra.abubaria.GamePanel.screenWidth
import d2t.terra.abubaria.entity.player.ClientPlayer
import lwjgl.drawString
import lwjgl.loadImage

object Hud {
    var healthBar = ""
    var inventory = ClientPlayer.inventory

    val path = "hud/inventory/"
    val selectedSlot = /*readImage(*/loadImage("${path}selectedSlot.png")/*)*/
    val hoveredSlot = /*readImage(*/loadImage("${path}hoveredSlot.png")/*)*/
    val slot = /*readImage(*/loadImage("${path}slot.png")/*)*/

    fun draw() {

        healthBar = ClientPlayer.run { "$health / $maxHealth" }

        drawString(healthBar, defaultScreenWidth - healthBar.length * 8, 15,4)
        inventory.draw()


    }
}