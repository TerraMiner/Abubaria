package d2t.terra.abubaria.hud

import d2t.terra.abubaria.entity.impl.ClientPlayer
import d2t.terra.abubaria.io.fonts.TextHorAligment
import d2t.terra.abubaria.io.fonts.TextHorPosition
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.io.graphics.Texture
import d2t.terra.abubaria.io.graphics.render.RenderDimension
import d2t.terra.abubaria.io.graphics.render.Renderer
import d2t.terra.abubaria.io.graphics.render.UI_HUD_TEXT_LAYER

object Hud {
    var healthBar = ""
    var inventory = ClientPlayer.inventory

    val path = "hud/inventory/"
    val selectedSlot = Texture.get("${path}selectedSlot.png")
    val slot = Texture.get("${path}slot.png")

    fun draw() {
        healthBar = ClientPlayer.run { "HP $health / $maxHealth" }

        Renderer.renderText(
            healthBar,
            Window.width.toFloat() - 4f,
            4f,
            25,
            textHorAligment = TextHorAligment.RIGHT,
            textHorPosition = TextHorPosition.RIGHT,
            zIndex = UI_HUD_TEXT_LAYER,
            dim = RenderDimension.SCREEN
        )

        inventory.draw()
    }
}