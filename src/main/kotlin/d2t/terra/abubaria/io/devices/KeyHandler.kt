package d2t.terra.abubaria.io.devices

import d2t.terra.abubaria.Client
import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.impl.ClientPlayer
import d2t.terra.abubaria.io.fonts.TextHorAligment
import d2t.terra.abubaria.io.fonts.TextHorPosition
import d2t.terra.abubaria.io.fonts.TextVerAlignment
import d2t.terra.abubaria.io.fonts.TextVerPosition
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

object KeyHandler {
    private val keyPressed = BooleanArray(350)
    private val keyActions = mutableMapOf<Int, () -> Unit>()
    private val keyPressActions = mutableMapOf<Int, () -> Unit>()
    private val keyPressedActions = mutableMapOf<Int, () -> Unit>()

    fun keyCallback(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        if (scancode !in keyPressed.indices) return
        if (action == GLFW_PRESS) {
            keyPressed[scancode] = true
            keyPressActions[scancode]?.invoke()
        } else if (action == GLFW_RELEASE) {
            keyPressed[scancode] = false
            keyActions[scancode]?.invoke()
        }
    }

    fun isKeyPressed(keyCode: Int) = keyPressed.getOrNull(keyCode) == true

    fun onKeyPress(key: Int, action: () -> Unit) {
        keyPressActions[key] = action
    }

    fun onKeyPressed(key: Int, action: () -> Unit) {
        keyPressedActions[key] = action
    }

    fun onKeyRelease(key: Int, action: () -> Unit) {
        keyActions[key] = action
    }

    fun update() {
        keyPressedActions.forEach { if (isKeyPressed(it.key)) it.value.invoke() }

    }

    init {
        onKeyPress(Keys.VK_RIGHT) {
            GamePanel.positionX = TextHorPosition.entries.getOrNull(GamePanel.positionX.ordinal - 1) ?: TextHorPosition.RIGHT
        }
        onKeyPress(Keys.VK_LEFT) {
            GamePanel.positionX = TextHorPosition.entries.getOrNull(GamePanel.positionX.ordinal + 1) ?: TextHorPosition.LEFT
        }
        onKeyPress(Keys.VK_UP) {
            GamePanel.positionY = TextVerPosition.entries.getOrNull(GamePanel.positionY.ordinal - 1) ?: TextVerPosition.BOTTOM
        }
        onKeyPress(Keys.VK_DOWN) {
            GamePanel.positionY = TextVerPosition.entries.getOrNull(GamePanel.positionY.ordinal + 1) ?: TextVerPosition.UP
        }
        onKeyPress(Keys.VK_DEL) {
            GamePanel.alignX = TextHorAligment.entries.getOrNull(GamePanel.alignX.ordinal - 1) ?: TextHorAligment.RIGHT
        }
        onKeyPress(Keys.VK_PGDN) {
            GamePanel.alignX = TextHorAligment.entries.getOrNull(GamePanel.alignX.ordinal + 1) ?: TextHorAligment.LEFT
        }
        onKeyPress(Keys.VK_HOME) {
            GamePanel.alignY = TextVerAlignment.entries.getOrNull(GamePanel.alignY.ordinal - 1) ?: TextVerAlignment.BOTTOM
        }
        onKeyPress(Keys.VK_END) {
            GamePanel.alignY = TextVerAlignment.entries.getOrNull(GamePanel.alignY.ordinal + 1) ?: TextVerAlignment.UP
        }
        onKeyPress(Keys.VK_F3) { Client.debugMode = !Client.debugMode }
        onKeyPress(Keys.VK_F9) { GamePanel.debug = true }
        onKeyPress(Keys.VK_B) { Client.lightMode = !Client.lightMode }

        onKeyPressed(Keys.VK_A, ClientPlayer::moveLeft)
        onKeyPressed(Keys.VK_D, ClientPlayer::moveRight)

        onKeyPress(Keys.VK_E) { ClientPlayer.inventory.opened = !ClientPlayer.inventory.opened }
        onKeyPressed(Keys.VK_1) { ClientPlayer.inventory.selectedHotBar = 0 }
        onKeyPressed(Keys.VK_2) { ClientPlayer.inventory.selectedHotBar = 1 }
        onKeyPressed(Keys.VK_3) { ClientPlayer.inventory.selectedHotBar = 2 }
        onKeyPressed(Keys.VK_4) { ClientPlayer.inventory.selectedHotBar = 3 }
        onKeyPressed(Keys.VK_5) { ClientPlayer.inventory.selectedHotBar = 4 }
        onKeyPressed(Keys.VK_6) { ClientPlayer.inventory.selectedHotBar = 5 }
        onKeyPressed(Keys.VK_7) { ClientPlayer.inventory.selectedHotBar = 6 }
        onKeyPressed(Keys.VK_8) { ClientPlayer.inventory.selectedHotBar = 7 }
        onKeyPressed(Keys.VK_9) { ClientPlayer.inventory.selectedHotBar = 8 }
        onKeyPressed(Keys.VK_0) { ClientPlayer.inventory.selectedHotBar = 9 }
    }
}
