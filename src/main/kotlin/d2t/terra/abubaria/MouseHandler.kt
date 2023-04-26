import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.player.ClientPlayer
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener

object MouseHandler : MouseListener, MouseWheelListener {

    val cursor = GamePanel.cursor

    override fun mouseClicked(e: MouseEvent?) {
    }

    override fun mousePressed(e: MouseEvent?) {
        if (e === null) return

        when (e.button) {
            1 -> {
                cursor.leftPress = true
                cursor.leftClick = true
            }
            2 -> {
                cursor.midPress = true
                cursor.midClick = true

            }
            3 -> {
                cursor.rightPress = true
                cursor.rightClick = true
            }
        }
    }

    override fun mouseReleased(e: MouseEvent?) {

        if (e === null) return

        when (e.button) {
            1 -> cursor.leftPress = false
            2 -> cursor.midPress = false
            3 -> cursor.rightPress = false
        }
    }

    override fun mouseEntered(e: MouseEvent?) {

        if (e === null) return

        cursor.mouseInWindow = true
    }

    override fun mouseExited(e: MouseEvent?) {

        if (e === null) return

        cursor.mouseInWindow = false

    }

    override fun mouseWheelMoved(e: MouseWheelEvent?) {
        if (e === null) return
        ClientPlayer.scrollHotBar(e.wheelRotation)
    }
}