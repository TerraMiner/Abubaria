package d2t.terra.abubaria.hud

import d2t.terra.abubaria.entity.impl.ClientPlayer
import d2t.terra.abubaria.io.fonts.TextHorAligment
import d2t.terra.abubaria.io.fonts.TextHorPosition
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.io.graphics.texture.Texture
import d2t.terra.abubaria.io.graphics.render.Layer
import d2t.terra.abubaria.io.graphics.render.RenderDimension
import d2t.terra.abubaria.io.graphics.render.Renderer

object Hud {
    var healthBar = ""
    var inventory = ClientPlayer.inventory

    val path = "hud/inventory/"
    val selectedSlot = Texture.get("${path}selectedSlot.png")
    val slot = Texture.get("${path}slot.png")

    fun render() {
        healthBar = ClientPlayer.run { "HP $health / $maxHealth" }

        Renderer.renderText(
            healthBar,
            Window.width.toFloat() - 4f,
            4f,
            25,
            horAlign = TextHorAligment.RIGHT,
            horPos = TextHorPosition.RIGHT,
            layer = Layer.UI_HUD_TEXT_LAYER,
            dim = RenderDimension.SCREEN
        )

        inventory.draw()
    }
}