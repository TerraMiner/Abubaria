package d2t.terra.abubaria.io.devices

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.player.ClientPlayer
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

object MouseHandler {
    private var scrollX: Double = .0
    private var scrollY: Double = .0
    private var xPos: Double = .0
    private var yPos: Double = .0
    private var lastX: Double = .0
    private var lastY: Double = .0
    private val mouseButtonPressed = BooleanArray(3)

    var isDragging = false

    val cursor = GamePanel.cursor

    fun mousePosCallback(window: Long, xps: Double, yps: Double) {
        lastX = xPos
        lastY = yPos
        xPos = xps
        yPos = yps
        isDragging = mouseButtonPressed.any { it }
    }

    fun mouseButtonCallback(window: Long, button: Int, action: Int, mods: Int) {
        if (button >= mouseButtonPressed.size) return
        if (action == GLFW_PRESS) {
            mouseButtonPressed[button] = true
        } else if (action == GLFW_RELEASE) {
            mouseButtonPressed[button] = false
            isDragging = false
        }
    }

    fun mouseScrollCallback(window: Long, xOffset: Double, yOffset: Double) {
        scrollX = xOffset
        scrollY = yOffset
    }

    fun endFrame() {
        scrollX = .0
        scrollY = .0
        lastX = xPos
        lastY = yPos
    }

    val x get() = xPos.toFloat()
    val y get() = yPos.toFloat()
    val dx get() = (lastX - xPos).toFloat()
    val dy get() = (lastY - yPos).toFloat()

    fun buttonIsPressed(button: Int): Boolean {
        if (button >= mouseButtonPressed.size) return false
        return mouseButtonPressed[button]
    }

    fun update() {

        if (scrollY != .0) {
            ClientPlayer.inventory.scrollHotBar(-scrollY.toInt())
            scrollY = .0
        }

        mouseButtonPressed.forEachIndexed { index, b ->

            when (index) {
                0 -> {
                    if (b && !cursor.leftPress) cursor.leftClick = true
                    cursor.leftPress = b
                }

                1 -> {
                    if (b && !cursor.rightPress) cursor.rightClick = true
                    cursor.rightPress = b
                }

                2 -> {
                    if (b && !cursor.midPress) cursor.midClick = true
                    cursor.midPress = b
                }

            }

        }
    }

    fun checkMouseInBound() {

    }

//    override fun mouseEntered(e: MouseEvent?) {
//
//        if (e === null) return
//
//        cursor.mouseInWindow = true
//    }
//
//    override fun mouseExited(e: MouseEvent?) {
//
//        if (e === null) return
//
//        cursor.mouseInWindow = false
//
//    }
//
//    override fun mouseWheelMoved(e: MouseWheelEvent?) {
//        if (e === null) return
//        ClientPlayer.scrollHotBar(e.wheelRotation)
//    }
}