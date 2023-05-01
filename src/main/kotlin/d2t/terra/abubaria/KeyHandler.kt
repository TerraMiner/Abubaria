import d2t.terra.abubaria.Client
import d2t.terra.abubaria.entity.player.ClientPlayer
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE
import java.awt.event.KeyEvent

object KeyHandler {
    private var keyPressed = BooleanArray(350)

    fun keyCallback(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        if (key >= keyPressed.size) return
        if (action == GLFW_PRESS) {
            keyPressed[key] = true
        } else if (action == GLFW_RELEASE) {
            keyPressed[key] = false
        }
    }

    fun isKeyPressed(keyCode: Int): Boolean {
        if (keyCode > keyPressed.size) return false
        return keyPressed[keyCode]
    }

    var upPressed = false
    var downPressed = false
    var rightPressed = false
    var leftPressed = false

    var spacePressed = false
    var timeSpacePressed = 0L
    var timeSpaceReleased = 0L


    fun update() {
        keyPressed.forEachIndexed { code, b ->
            when (code) {

                KeyEvent.VK_W -> {
                    upPressed = b
                }

                KeyEvent.VK_S -> {
                    downPressed = b
                }

                KeyEvent.VK_A -> {
                    leftPressed = b
                }

                KeyEvent.VK_D -> {
                    rightPressed = b
                }

                KeyEvent.VK_SPACE -> {
                    if (b) {
                        if (!spacePressed) {
                            spacePressed = true
                            timeSpacePressed = System.currentTimeMillis()
                            timeSpaceReleased = System.currentTimeMillis() + 1000
                        }
                    } else {
                        spacePressed = false
                        if (timeSpaceReleased <= timeSpacePressed + 1000) {
                            timeSpaceReleased = System.currentTimeMillis()
                        }
                    }
                }

                KeyEvent.VK_F3 -> {
                    Client.debugMode = !Client.debugMode
                }

//            KeyEvent.VK_F11 -> {
//                GamePanel.setFullScreen(!GamePanel.inFullScreen)
//            }

                KeyEvent.VK_E -> {
                    if (b) {
                        ClientPlayer.inventory.opened = !ClientPlayer.inventory.opened
                        keyPressed[code] = false
                    }
                }
            }
        }
    }
}