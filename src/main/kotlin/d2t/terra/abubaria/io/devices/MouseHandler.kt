package d2t.terra.abubaria.io.devices

import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

object MouseHandler {
    private val mouseButtonPressed = BooleanArray(10)
    private val mouseButtonReleaseActions = mutableMapOf<Int, (Int, Int) -> Unit>()
    private val mouseButtonClickActions = mutableMapOf<Int, (Int, Int) -> Unit>()
    private val mouseButtonPressedActions = mutableMapOf<Int, (Int, Int) -> Unit>()
    private val mouseScrollActions = mutableListOf<(Double) -> Unit>()

    private var xPos: Int = 0
    private var yPos: Int = 0
    private var lastX: Int = 0
    private var lastY: Int = 0

    fun mousePosCallback(window: Long, xps: Double, yps: Double) {
        lastX = xPos
        lastY = yPos
        xPos = xps.toInt()
        yPos = yps.toInt()
    }

    fun update() {
        mouseButtonPressed.forEachIndexed { index, bool ->
            if (bool) mouseButtonPressedActions[index]?.invoke(xPos, yPos)
        }
    }

    fun mouseButtonCallback(window: Long, button: Int, action: Int, mods: Int) {
        if (button !in mouseButtonPressed.indices) return

        if (action == GLFW_PRESS) {
            mouseButtonPressed[button] = true
            mouseButtonClickActions[button]?.invoke(xPos, yPos)
        } else if (action == GLFW_RELEASE) {
            mouseButtonPressed[button] = false
            mouseButtonReleaseActions[button]?.invoke(xPos, yPos)
        }
    }

    fun forceReleaseButton(button: Int) {
        mouseButtonPressed[button] = false
    }

    fun mouseScrollCallback(window: Long, xOffset: Double, yOffset: Double) {
        if (yOffset != 0.0) mouseScrollActions.forEach {
            it.invoke(yOffset)
        }
    }

    fun onMouseClick(button: Int, action: (Int, Int) -> Unit) {
        mouseButtonClickActions[button] = action
    }

    fun onMouseRelease(button: Int, action: (Int, Int) -> Unit) {
        mouseButtonReleaseActions[button] = action
    }

    fun onMouseScroll(action: (Double) -> Unit) {
        mouseScrollActions.add(action)
    }

    fun onMousePress(button: Int, action: (Int, Int) -> Unit) {
        mouseButtonPressedActions[button] = action
    }

    fun isButtonPressed(button: Int): Boolean {
        if (button !in mouseButtonPressed.indices) return false
        return mouseButtonPressed[button]
    }

    val x get() = xPos.toFloat()
    val y get() = yPos.toFloat()
    val dx get() = (lastX - xPos).toFloat()
    val dy get() = (lastY - yPos).toFloat()
}