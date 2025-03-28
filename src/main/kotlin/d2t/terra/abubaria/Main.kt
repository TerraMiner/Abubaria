package d2t.terra.abubaria

import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.util.TaskScheduler
import org.lwjgl.glfw.GLFW.glfwInit


fun main() {
    if (!glfwInit()) {
        throw IllegalStateException("Failed to initialize GLFW")
    }

    Window.open()

    TaskScheduler.initialize()

    GamePanel.startGame()

    close()
}

fun close() {
    GamePanel.gameThread?.interrupt()
    GamePanel.gameThread?.join()
    TaskScheduler.shutdown()
    Window.close()
}