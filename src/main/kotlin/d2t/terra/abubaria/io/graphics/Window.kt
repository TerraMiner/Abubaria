package d2t.terra.abubaria.io.graphics

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.io.devices.KeyHandler
import d2t.terra.abubaria.io.devices.MouseHandler
import d2t.terra.abubaria.io.graphics.render.RendererManager
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.GLFW_CURSOR
import org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN
import org.lwjgl.glfw.GLFW.glfwCreateWindow
import org.lwjgl.glfw.GLFW.glfwDestroyWindow
import org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor
import org.lwjgl.glfw.GLFW.glfwGetVideoMode
import org.lwjgl.glfw.GLFW.glfwMakeContextCurrent
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback
import org.lwjgl.glfw.GLFW.glfwSetErrorCallback
import org.lwjgl.glfw.GLFW.glfwSetInputMode
import org.lwjgl.glfw.GLFW.glfwSetKeyCallback
import org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback
import org.lwjgl.glfw.GLFW.glfwSetScrollCallback
import org.lwjgl.glfw.GLFW.glfwSetWindowPos
import org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose
import org.lwjgl.glfw.GLFW.glfwSetWindowSize
import org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback
import org.lwjgl.glfw.GLFW.glfwShowWindow
import org.lwjgl.glfw.GLFW.glfwSwapBuffers
import org.lwjgl.glfw.GLFW.glfwTerminate
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWWindowSizeCallback
import org.lwjgl.opengl.GL.createCapabilities
import org.lwjgl.opengl.GL11.GL_BLEND
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA
import org.lwjgl.opengl.GL11.GL_SRC_ALPHA
import org.lwjgl.opengl.GL11.GL_TEXTURE_2D
import org.lwjgl.opengl.GL11.glBlendFunc
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.opengl.GL11.glClearColor
import org.lwjgl.opengl.GL11.glEnable
import org.lwjgl.opengl.GL11.glViewport
import org.lwjgl.system.MemoryUtil.NULL

object Window {
    var windowId: Long = 0
    var width = 900
    var height = 600
    var centerX = width / 2.0
    var centerY = height / 2.0

    val bgColor = Color(150, 200, 250)

    private var fullScreen = false
    private val monitor = glfwGetPrimaryMonitor()
    val isFullScreenMonitor get() = if (fullScreen) glfwGetPrimaryMonitor() else 0

    var fpsLimit = 99999
    private val frameCap = 1.0 / fpsLimit

    private var frameTime = .0
    private var frames = 0

    private var time = System.nanoTime().toDouble() / 1000000000.0
    private var unprocessed = .0

    fun open() {
        glfwSetErrorCallback(object : GLFWErrorCallback() {
            override fun invoke(error: Int, description: Long) {
                throw IllegalStateException(getDescription(description))
            }
        })

        GLFWErrorCallback.createPrint(System.err).set()

        windowId = glfwCreateWindow(width, height, "Abubaria", isFullScreenMonitor, 0)
        if (windowId == NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_HIDDEN)

        if (!fullScreen) {
            val vid = glfwGetVideoMode(monitor)!!
            glfwSetWindowPos(windowId, (vid.width() - width) / 2, (vid.height() - height) / 2)
        }
        glfwShowWindow(windowId)

        glfwMakeContextCurrent(windowId)

        createCapabilities()

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        setCallbacks()

        glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a)

        glEnable(GL_TEXTURE_2D)

        RendererManager.setup()
    }

    fun draw(action: () -> Unit) {
        while (!glfwWindowShouldClose(windowId)) {

            var canRender = false
            val time2 = System.nanoTime().toDouble() / 1000000000.0
            val passed = time2 - time
            unprocessed += passed
            frameTime += passed

            time = time2
            while (unprocessed >= frameCap) {
                unprocessed -= frameCap
                canRender = true
                glfwPollEvents()
                if (frameTime >= 1.0) {
                    frameTime = .0
                    GamePanel.display.fps = frames
                    frames = 0
                }
            }

            if (canRender) {
                glClear(GL_COLOR_BUFFER_BIT)
                action()
                glfwSwapBuffers(windowId)
                frames++
            }
        }
    }

    fun whileWindowOpened(action: () -> Unit) {
        while (!glfwWindowShouldClose(windowId)) {
            action()
        }
    }

    fun close() {
        glfwFreeCallbacks(windowId)
        glfwSetErrorCallback(null)?.free()
        glfwSetWindowShouldClose(windowId, true)
        glfwTerminate()
        glfwDestroyWindow(windowId)
    }

    private fun setCallbacks() {

        glfwSetCursorPosCallback(windowId, MouseHandler::mousePosCallback)
        glfwSetMouseButtonCallback(windowId, MouseHandler::mouseButtonCallback)
        glfwSetScrollCallback(windowId, MouseHandler::mouseScrollCallback)
        glfwSetKeyCallback(windowId, KeyHandler::keyCallback)

        glfwSetWindowSizeCallback(windowId, object : GLFWWindowSizeCallback() {
            override fun invoke(argWindow: Long, argWidth: Int, argHeight: Int) {
                val correctedWidth = argWidth.coerceIn(800..Int.MAX_VALUE)
                val correctedHeight = argHeight.coerceIn(600..Int.MAX_VALUE)
                glfwSetWindowSize(windowId, correctedWidth, correctedHeight)
                width = correctedWidth
                height = correctedHeight
                centerX = width / 2.0
                centerY = height / 2.0
                glViewport(0, 0, width, height)
                RendererManager.updateProjections()
            }
        })
    }

}