package d2t.terra.abubaria

import d2t.terra.abubaria.entity.impl.ClientPlayer
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.io.graphics.render.Layer
import d2t.terra.abubaria.io.graphics.render.RenderDimension
import d2t.terra.abubaria.io.graphics.render.Renderer
import java.util.*

object DebugDisplay {
    var fps = 0
    var tps = 0
    var lps = 0
    var bgp = 0

    val fpsText get() = "FPS: $fps"

    val text
        get() = StringJoiner("\n")
            .add(fpsText).add("TPS: $tps").add("LPS: $lps").add("BufferGets: $bgp")
            .add("Pos: ${ClientPlayer.location.x} ${ClientPlayer.location.y}")
            .add("Vsync: ${Window.vsync}")
            .add("FPS Limit: ${Window.fpsLimit}")
            .add("movement: ${ClientPlayer.movement.x}, ${ClientPlayer.movement.y}")
            .add("onGround: ${ClientPlayer.isOnGround}")
            .add("onJump: ${ClientPlayer.onJump}")
            .add("entities: ${GamePanel.world.entities.size}")
            .add("videoLag: ${GamePanel.videoLag}")
            .toString()

    fun render() {
        val layer = Layer.UI_DEBUG_LAYER
        val dim = RenderDimension.SCREEN
        if (Client.showDebugDisplay) Renderer.renderText(text, 4f, 4f, 25, layer = layer, dim = dim)
        else Renderer.renderText(fpsText, 4f, 4f, 25, layer = layer, dim = dim)
    }
}