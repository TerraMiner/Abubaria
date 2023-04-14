import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.player.Player
import java.util.*

class DebugDisplay(val player: Player) {
    var fps = 0
    var tps = 0

    val text get() = StringJoiner("\n")
        .add("FPS: $fps")
        .add("TPS: $tps")
        .add("Pos: ${player.location.x} ${player.location.y}")
        .add("Speed: ${player.dxModifier}")
        .add("dX: ${player.dx}")
        .add("dY: ${player.dy}")
        .add("ground: ${player.ground}")
//        .add("onWorldBorder: ${player.onWorldBorder}")
        .add("onGround: ${player.onGround}")
        .add("onJump: ${player.onJump}")
        .add("videoLag: ${GamePanel.videoLag}")
        .toString()

}