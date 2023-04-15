import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.player.Camera
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

object KeyHandler : KeyListener {

    var upPressed = false
    var downPressed = false
    var rightPressed = false
    var leftPressed = false

    var spacePressed = false
    var timeSpacePressed = 0L
    var timeSpaceReleased = 0L


    override fun keyTyped(e: KeyEvent?) {

    }

    override fun keyPressed(e: KeyEvent?) {
        val code = e?.keyCode ?: return
        when (code) {

            KeyEvent.VK_W -> {
                upPressed = true
            }

            KeyEvent.VK_S -> {
                downPressed = true
            }

            KeyEvent.VK_A -> {
                leftPressed = true
            }

            KeyEvent.VK_D -> {
                rightPressed = true
            }

            KeyEvent.VK_SPACE -> {
                if (!spacePressed) {
                    spacePressed = true
                    timeSpacePressed = System.currentTimeMillis()
                    timeSpaceReleased = System.currentTimeMillis() + 1000
                }
            }

            KeyEvent.VK_F3 -> {
                Client.debugMode = !Client.debugMode
            }

            KeyEvent.VK_F11 -> {
                GamePanel.setFullScreen(!GamePanel.inFullScreen)
            }
        }
    }

    override fun keyReleased(e: KeyEvent?) {
        val code = e?.keyCode ?: return
        when (code) {

            KeyEvent.VK_W -> {
                upPressed = false
            }

            KeyEvent.VK_S -> {
                downPressed = false
            }

            KeyEvent.VK_A -> {
                leftPressed = false
            }

            KeyEvent.VK_D -> {
                rightPressed = false
            }

            KeyEvent.VK_SPACE -> {
                spacePressed = false
                if (timeSpaceReleased <= timeSpacePressed + 1000) {
                    timeSpaceReleased = System.currentTimeMillis()
                }

            }

            KeyEvent.VK_B -> {}

        }
    }
}