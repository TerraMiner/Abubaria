import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.io.graphics.drawString
import d2t.terra.abubaria.io.graphics.safetyTextures
import java.awt.Color
import java.util.*

class DebugDisplay {
    var fps = 0
    var tps = 0

    val text get() = StringJoiner("\n")
        .add("FPS: $fps")
        .add("TPS: $tps")
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
        safetyTextures {
            text.apply {
                split("\n").forEachIndexed { index, text ->
                    val y = index * 20 + 20
                    if (index == 0)
                        drawString(text, 4, y, 3, Color.WHITE)
                    else if (Client.debugMode)
                        drawString(text, 4, y, 3, Color.WHITE)
                }
            }
        }
    }

}