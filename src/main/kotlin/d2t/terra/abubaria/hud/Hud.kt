package d2t.terra.abubaria.hud

import d2t.terra.abubaria.GamePanel.defaultScreenWidth
import d2t.terra.abubaria.GamePanel.screenWidth
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.lwjgl.drawString
import d2t.terra.abubaria.lwjgl.loadImage

object Hud {
    var healthBar = ""
    var inventory = ClientPlayer.inventory

    val path = "hud/inventory/"
    val selectedSlot = loadImage("${path}selectedSlot.png")
    val slot = loadImage("${path}slot.png")

    fun draw() {

        healthBar = ClientPlayer.run { "HP $health / $maxHealth" }

        drawString(healthBar, screenWidth - healthBar.length * 12, 15,3)
        inventory.draw()


    }
}