package vbotests.io

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback

class Window(
    var title: String,
    var width: Int = 640,
    var height: Int = 480
) {
    private var _window: Long = 0L
    private val monitor = glfwGetPrimaryMonitor()
    private var fullScreen = false

    lateinit var input: Input

    val isFullScreenMonitor get() = if (fullScreen) glfwGetPrimaryMonitor() else 0

    fun createWindow() {
        _window = glfwCreateWindow(width, height, title, isFullScreenMonitor, 0)

        if (_window == 0L) {
            throw IllegalStateException("Failed to create window!")
        }

        if (!fullScreen) {
            val vid = glfwGetVideoMode(monitor)!!
            glfwSetWindowPos(_window, (vid.width() - width) / 2, (vid.height() - height) / 2)
        }

        glfwShowWindow(_window)

        glfwMakeContextCurrent(_window)

        input = Input(id)
    }

    fun shouldClose() = glfwWindowShouldClose(_window)
    fun swapBuffers() {
        glfwSwapBuffers(_window)
    }
    fun setSize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    fun getFullScreen() = fullScreen
    fun setFullScreen(value: Boolean) {
        fullScreen = value
    }

    fun update() {
        input.update()
        glfwPollEvents()
    }

    val id get() = _window

    companion object {
        fun setCallBacks() {
            glfwSetErrorCallback(object : GLFWErrorCallback() {
                override fun invoke(error: Int, description: Long) {
                    throw IllegalStateException(getDescription(description))
                }
            })
        }
    }
}