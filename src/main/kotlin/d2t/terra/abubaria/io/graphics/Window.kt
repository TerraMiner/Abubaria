package d2t.terra.abubaria.io.graphics

import d2t.terra.abubaria.DebugDisplay
import d2t.terra.abubaria.io.devices.KeyHandler
import d2t.terra.abubaria.io.devices.MouseHandler
import d2t.terra.abubaria.io.graphics.render.Renderer
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.GLFW_CURSOR
import org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN
import org.lwjgl.glfw.GLFW.GLFW_STENCIL_BITS
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
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_TEXTURE_2D
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.opengl.GL11.glClearColor
import org.lwjgl.opengl.GL11.glEnable
import org.lwjgl.opengl.GL11.glViewport
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.glfw.GLFW.glfwSetWindowIcon
import org.lwjgl.glfw.GLFW.glfwSwapInterval
import org.lwjgl.glfw.GLFW.glfwWindowHint
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_STENCIL_TEST
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.system.MemoryStack
import java.io.File
import java.io.FileOutputStream

object Window {
    var windowId: Long = 0
    var width = 900
    var height = 600
    var centerX = width / 2f
    var centerY = height / 2f

    val bgColor = Color(150, 200, 250)

    private var fullScreen = false
    private val monitor = glfwGetPrimaryMonitor()
    val isFullScreenMonitor get() = if (fullScreen) glfwGetPrimaryMonitor() else 0

    lateinit var videoMode: GLFWVidMode

    var vsync: Boolean = false
        set(value) {
            field = value
            glfwSwapInterval(if (value) 1 else 0)
        }

    const val MAX_FPS_LIMIT = 99999
    var fpsLimit = MAX_FPS_LIMIT
        get() = if (vsync) videoMode.refreshRate() else field
        set(value) {
            field = value.coerceIn(5,MAX_FPS_LIMIT)
        }

    private val frameCap get() = 1.0 / fpsLimit

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

        glfwWindowHint(GLFW_STENCIL_BITS, 8)

        windowId = glfwCreateWindow(width, height, "Abubaria", isFullScreenMonitor, 0)
        if (windowId == NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_HIDDEN)
        videoMode = glfwGetVideoMode(monitor)!!
        if (!fullScreen) {
            glfwSetWindowPos(windowId, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2)
        }
        glfwShowWindow(windowId)

        glfwMakeContextCurrent(windowId)

        createCapabilities()

        setCallbacks()

        glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a)

        glEnable(GL_TEXTURE_2D)

        Renderer.register()

        vsync = false

        setWindowIcon()

        glEnable(GL_STENCIL_TEST)
    }

    fun startDrawLoop(frameAction: () -> Unit) {
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
                    DebugDisplay.fps = frames
                    frames = 0
                }
            }

            if (canRender) {
                glClear(GL_COLOR_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)
                frameAction()
                glfwSwapBuffers(windowId)
                frames++
            }
        }
    }

    fun close() {
        LightRenderer.cleanup()
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
                centerX = width / 2f
                centerY = height / 2f
                glViewport(0, 0, width, height)
                Renderer.updateProjection()
                LightRenderer.updateProjection()
            }
        })
    }

    private fun setWindowIcon() {
        MemoryStack.stackPush().use { stack ->
            val w = stack.mallocInt(1)
            val h = stack.mallocInt(1)
            val channels = stack.mallocInt(1)

            val iconSizes = intArrayOf(16, 32, 48, 64)
            val icons = GLFWImage.malloc(iconSizes.size)

            var validIcons = 0
            iconSizes.forEachIndexed { index, size ->
                val iconStream = Window::class.java.getResourceAsStream("/icons/icon_${size}x${size}.png")
                if (iconStream != null) {
                    val tempFile = File.createTempFile("icon_${size}", ".png")
                    tempFile.deleteOnExit()
                    iconStream.use { input ->
                        FileOutputStream(tempFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    val iconBuffer = stbi_load(tempFile.absolutePath, w, h, channels, 4)
                    if (iconBuffer != null) {
                        icons[index].width(w.get(0))
                        icons[index].height(h.get(0))
                        icons[index].pixels(iconBuffer)
                        validIcons++
                    }
                }
            }

            if (validIcons > 0) {
                glfwSetWindowIcon(windowId, icons)
            }

            icons.free()
        }
    }

}