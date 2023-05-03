package d2t.terra.abubaria

import d2t.terra.abubaria.io.devices.KeyHandler
import d2t.terra.abubaria.io.TextRender
import d2t.terra.abubaria.io.devices.MouseHandler
import d2t.terra.abubaria.io.fonts.CFont
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWWindowPosCallback
import org.lwjgl.glfw.GLFWWindowSizeCallback
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryUtil.NULL


var window: Long = 0

fun main() {
    val width = 640
    val height = 480

    if (!glfwInit()) {
        throw IllegalStateException("Failed to initialize GLFW")
    }

    window = glfwCreateWindow(width, height, "Abubaria", NULL, NULL)
    if (window == NULL) {
        throw RuntimeException("Failed to create the GLFW window")
    }

    GLFWErrorCallback.createPrint(System.err).set()

    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN)

    glfwMakeContextCurrent(window)

    GL.createCapabilities()

    GamePanel.setupScreen()

    setCallbacks()

    GamePanel.startGameThread()

    glfwFreeCallbacks(window)
    glfwDestroyWindow(window)
    glfwSetErrorCallback(null)?.free()
    glfwTerminate()
}

private fun setCallbacks() {

    glfwSetCursorPosCallback(window, MouseHandler::mousePosCallback)
    glfwSetMouseButtonCallback(window, MouseHandler::mouseButtonCallback)
    glfwSetScrollCallback(window, MouseHandler::mouseScrollCallback)
    glfwSetKeyCallback(window, KeyHandler::keyCallback)

    glfwSetWindowSizeCallback(window, object : GLFWWindowSizeCallback() {
        override fun invoke(argWindow: Long, argWidth: Int, argHeight: Int) {

            GamePanel.setupScreen()

//            println("WORK")
//            GamePanel.screenWidth2 = argWidth
//            GamePanel.screenHeight2 = argHeight
//            GamePanel.hasResized = true
        }
    })
    glfwSetWindowPosCallback(window, object : GLFWWindowPosCallback() {
        override fun invoke(window: Long, xpos: Int, ypos: Int) {
//            GamePanel.setupScreen()
        }
    })
}

fun main1() {
//    window.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
//
//    window.title = "Abubaria"

//    val cursorImg = BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)

//    window.contentPane.cursor = Toolkit.getDefaultToolkit().createCustomCursor(
//        cursorImg, Point(0, 0), "blank cursor"
//    )

//    window.add(GamePanel)

//    window.pack()

//    window.setLocationRelativeTo(null)

//    window.isVisible = true

//    GamePanel.setupScreen()

//    window.addComponentListener(object : ComponentListener {
//        override fun componentResized(e: ComponentEvent?) {
//            GamePanel.setupScreen()
//        }

//        override fun componentMoved(e: ComponentEvent?) {
//            GamePanel.setupScreen()
//        }

//        override fun componentShown(e: ComponentEvent?) {
//            GamePanel.setupScreen()
//        }

//        override fun componentHidden(e: ComponentEvent?) {
//            GamePanel.setupScreen()
//        }

//    })

//    GamePanel.startGameThread()
}



