import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.player.ClientPlayer
import java.util.*

class DebugDisplay {
    var fps = 0
    var tps = 0

    val text get() = StringJoiner("\n")
        .add("FPS: $fps")
        .add("TPS: $tps")
        .add("Pos: ${ClientPlayer.location.x} ${ClientPlayer.location.y}")
        .add("Speed: ${ClientPlayer.dxModifier}")
        .add("dX: ${ClientPlayer.dx}")
        .add("dY: ${ClientPlayer.dy}")
        .add("ground: ${ClientPlayer.ground}")
        .add("onGround: ${ClientPlayer.onGround}")
        .add("onJump: ${ClientPlayer.onJump}")
        .add("videoLag: ${GamePanel.videoLag}")
        .toString()

}