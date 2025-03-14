package d2t.terra.abubaria

import d2t.terra.abubaria.io.graphics.Window
import org.lwjgl.glfw.GLFW.glfwInit


fun main() {
    if (!glfwInit()) {
        throw IllegalStateException("Failed to initialize GLFW")
    }

    Window.open()

    GamePanel.startGameThread()

    close()
}

fun close() {
    GamePanel.gameThread?.interrupt()
    GamePanel.lightThread?.interrupt()
    GamePanel.service.shutdown()
    GamePanel.gameThread?.join()
    GamePanel.lightThread?.join()
    Window.close()
}