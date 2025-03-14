package d2t.terra.abubaria

import d2t.terra.abubaria.entity.player.ClientPlayer
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
            .add("dX: ${ClientPlayer.dx}")
            .add("dY: ${ClientPlayer.dy}")
            .add("ground: ${ClientPlayer.ground}")
            .add("onGround: ${ClientPlayer.onGround}")
            .add("onJump: ${ClientPlayer.onJump}")
            .add("entities: ${GamePanel.world.entities.size}")
            .add("videoLag: ${GamePanel.videoLag}")
            .toString()

    fun draw() {
        text.apply {
            if (Client.debugMode) RendererManager.UIRenderer.renderText(text, 4f, 4f, .4f)
            else RendererManager.UIRenderer.renderText(split("\n")[0],4f,4f,.4f)
        }
    }


}