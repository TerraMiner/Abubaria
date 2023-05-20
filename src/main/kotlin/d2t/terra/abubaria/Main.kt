package d2t.terra.abubaria

import d2t.terra.abubaria.io.devices.KeyHandler
import d2t.terra.abubaria.io.devices.MouseHandler
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWWindowSizeCallback
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryUtil.NULL
import kotlin.system.exitProcess

var window: Long = 0
val windowWidth = 1280
val windowHeight = 560

fun main() {

    if (!glfwInit()) {
        throw IllegalStateException("Failed to initialize GLFW")
    }

    window = glfwCreateWindow(windowWidth, windowHeight, "Abubaria", NULL, NULL)
    if (window == NULL) {
        throw RuntimeException("Failed to create the GLFW window")
    }

    GLFWErrorCallback.createPrint(System.err).set()

    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN)

    glfwMakeContextCurrent(window)

    GL.createCapabilities()

    glfwSwapInterval(1)

    GamePanel.setupScreen()

    setCallbacks()

    GamePanel.startGameThread()

    close()
}

fun close() {
    GamePanel.gameThread?.interrupt()
    GamePanel.lightThread?.interrupt()
    GamePanel.service.shutdown()
    GamePanel.gameThread?.join()
    GamePanel.lightThread?.join()
    glfwFreeCallbacks(window)
    glfwSetErrorCallback(null)?.free()
    glfwSetWindowShouldClose(window, true)
    glfwTerminate()
    glfwDestroyWindow(window)
}

private fun setCallbacks() {

    glfwSetCursorPosCallback(window, MouseHandler::mousePosCallback)
    glfwSetMouseButtonCallback(window, MouseHandler::mouseButtonCallback)
    glfwSetScrollCallback(window, MouseHandler::mouseScrollCallback)
    glfwSetKeyCallback(window, KeyHandler::keyCallback)

    glfwSetWindowSizeCallback(window, object : GLFWWindowSizeCallback() {
        override fun invoke(argWindow: Long, argWidth: Int, argHeight: Int) {
            val width = argWidth.coerceIn(800..Int.MAX_VALUE)
            val height = argHeight.coerceIn(600..Int.MAX_VALUE)

            glfwSetWindowSize(window, width, height)

            GamePanel.setupScreen()
        }
    })
}