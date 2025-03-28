package d2t.terra.abubaria.hud

import d2t.terra.abubaria.entity.impl.ClientPlayer
import d2t.terra.abubaria.io.fonts.TextHorAligment
import d2t.terra.abubaria.io.fonts.TextHorPosition
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.io.graphics.Texture
import d2t.terra.abubaria.io.graphics.render.RendererManager

object Hud {
    var healthBar = ""
    var inventory = ClientPlayer.inventory

    val path = "hud/inventory/"
    val selectedSlot = Texture("${path}selectedSlot.png")
    val slot = Texture("${path}slot.png")

    fun draw() {
        healthBar = ClientPlayer.run { "HP $health / $maxHealth" }

        RendererManager.UIRenderer.renderText(
            healthBar,
            Window.width.toFloat() - 4f,
            4f,
            .4f,
            textHorAligment = TextHorAligment.RIGHT,
            textHorPosition = TextHorPosition.RIGHT
        )

        inventory.draw()
    }
}