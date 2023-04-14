import d2t.terra.abubaria.GamePanel
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

object MouseHandler : MouseListener {

    val cursor = GamePanel.cursor

    override fun mouseClicked(e: MouseEvent?) {
//        println("mouse clicked")
    }

    override fun mousePressed(e: MouseEvent?) {
        if (e == null) return

        when (e.button) {
            1 -> cursor.leftClick = true
            2 -> cursor.midClick = true
            3 -> cursor.rightClick = true
        }
    }

    override fun mouseReleased(e: MouseEvent?) {

        if (e == null) return

        when (e.button) {
            1 -> cursor.leftClick = false
            2 -> cursor.midClick = false
            3 -> cursor.rightClick = false
        }
    }

    override fun mouseEntered(e: MouseEvent?) {

        if (e == null) return

        cursor.mouseInWindow = true
    }

    override fun mouseExited(e: MouseEvent?) {

        if (e == null) return

        cursor.mouseInWindow = false
    }
}