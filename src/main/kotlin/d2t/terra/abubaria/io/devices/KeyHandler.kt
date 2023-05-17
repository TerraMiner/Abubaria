package d2t.terra.abubaria.io.devices

import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.player.ClientPlayer
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

object KeyHandler {
    private var keyPressed = BooleanArray(350)

    fun keyCallback(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        if (scancode >= keyPressed.size) return
        if (action == GLFW_PRESS) {
            keyPressed[scancode] = true
        } else if (action == GLFW_RELEASE) {
            keyPressed[scancode] = false
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
        keyPressed.forEachIndexed { code, pressed ->

            when (code) {

                Keys.VK_W -> {
                    upPressed = pressed
                }

                Keys.VK_S -> {
                    downPressed = pressed
                }

                Keys.VK_A -> {
                    leftPressed = pressed
                }

                Keys.VK_D -> {
                    rightPressed = pressed
                }

                Keys.VK_SPACE -> {
                    if (pressed) {
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

                Keys.VK_F3 -> {

                    if (pressed) {
                        Client.debugMode = !Client.debugMode
                        keyPressed[code] = false
                    }
                }

                Keys.VK_F9 -> {
                    if (pressed) {
                        GamePanel.debug = true
                        keyPressed[code] = false
                    }
                }

                Keys.VK_UP -> {
                    if (pressed) Client.zoomIn()
                }

                Keys.VK_DOWN -> {
                    if (pressed) Client.zoomOut()
                }

//            Keys.VK_F11 -> {
//                GamePanel.setFullScreen(!GamePanel.inFullScreen)
//            }
                Keys.VK_B -> {
                    if (pressed) {
                        Client.lightMode = !Client.lightMode
                        keyPressed[code] = false
                    }
                }

                Keys.VK_E -> {
                    if (pressed) {
                        ClientPlayer.inventory.opened = !ClientPlayer.inventory.opened
                        keyPressed[code] = false
                    }
                }
            }
        }
    }
}