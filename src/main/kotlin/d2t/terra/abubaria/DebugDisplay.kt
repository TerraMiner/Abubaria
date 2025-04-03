package d2t.terra.abubaria

import d2t.terra.abubaria.entity.impl.ClientPlayer
import d2t.terra.abubaria.io.devices.MouseHandler
import d2t.terra.abubaria.io.graphics.Texture
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.io.graphics.render.RenderDimension
import d2t.terra.abubaria.io.graphics.render.Renderer
import d2t.terra.abubaria.io.graphics.render.UI_DEBUG_LAYER
import d2t.terra.abubaria.world.Camera
import java.util.*

class DebugDisplay {
    var fps = 0
    var tps = 0
    var lps = 0

    val text
        get() = StringJoiner("\n")
            .add("FPS: $fps").add("TPS: $tps").add("LPS: $lps")
            .add("Pos: ${ClientPlayer.location.x} ${ClientPlayer.location.y}")
            .add("Vsync: ${Window.vsync}")
            .add("FPS Limit: ${Window.fpsLimit}")
            .add("movement: ${String.format("%.2f", ClientPlayer.movement.x)}, ${String.format("%.2f", ClientPlayer.movement.y)}")
            .add("onGround: ${ClientPlayer.isOnGround}")
            .add("onJump: ${ClientPlayer.onJump}")
            .add("entities: ${GamePanel.world.entities.size}")
            .add("videoLag: ${GamePanel.videoLag}")
            .toString()

    fun draw() {
        text.apply {
            if (Client.debugMode) Renderer.renderText(text, 4f, 4f, 25, zIndex = UI_DEBUG_LAYER, dim = RenderDimension.SCREEN)
            else Renderer.renderText(split("\n")[0], 4f, 4f, 25, zIndex = UI_DEBUG_LAYER, dim = RenderDimension.SCREEN)
        }
    }
}