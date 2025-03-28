package d2t.terra.abubaria

import d2t.terra.abubaria.entity.EntityService
import d2t.terra.abubaria.entity.impl.ClientPlayer
import d2t.terra.abubaria.io.graphics.render.RendererManager
import java.util.*

class DebugDisplay {
    var fps = 0
    var tps = 0
    var lps = 0

    val text
        get() = StringJoiner("\n")
            .add("FPS: $fps").add("TPS: $tps").add("LPS: $lps")
            .add("Pos: ${ClientPlayer.location.x} ${ClientPlayer.location.y}")
            .add("movement: ${String.format("%.2f", ClientPlayer.movement.x)}, ${String.format("%.2f", ClientPlayer.movement.y)}")
            .add("onGround: ${ClientPlayer.isOnGround}")
            .add("onJump: ${ClientPlayer.onJump}")
            .add("entities: ${EntityService.Entities.size}")
            .add("videoLag: ${GamePanel.videoLag}")
            .toString()

    fun draw() {
        text.apply {
            if (Client.debugMode) RendererManager.UIRenderer.renderText(text, 4f, 4f, .4f)
            else RendererManager.UIRenderer.renderText(split("\n")[0],4f,4f,.4f)
        }
    }
}