import d2t.terra.abubaria.util.loopIndicy
import d2t.terra.abubaria.util.loopWhile
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import java.awt.Point
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.thread
import kotlin.math.floor
import kotlin.random.Random

class PixelMapRenderer(val windowWidth: Int, val windowHeight: Int,val width: Int, val height: Int, action: PixelMapRenderer.() -> Unit) {

    private var window: Long = 0
    private var mouseX: Double = 0.0
    private var mouseY: Double = 0.0
    private var lastMouseX: Double = 0.0
    private var lastMouseY: Double = 0.0
    private var offsetX: Double = .0
    private var offsetY: Double = .0
    private var zoomLevel: Double = 1.0
    private var isRightMouseDown = false
    private var isLeftMouseDown = false
    private var isControlPressed = false
    var isSpacePressed = false

    private val pixelSize = 20
    private val clearColor = floatArrayOf(0.2f, 0.2f, 0.2f, 1.0f)

    var pixelMap: ConcurrentHashMap<Int, ConcurrentHashMap<Int, Boolean>> = ConcurrentHashMap(buildMap {
        repeat(width) { x ->
            put(x, ConcurrentHashMap(buildMap {
                repeat(height) {
                    put(it, false)
                }
            }))
        }
    })

    init {
        init()
        action(this)
        loop()
        cleanup()
    }

    private fun init() {
        GLFWErrorCallback.createPrint(System.err).set()

        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)

        window = glfwCreateWindow(windowWidth, windowHeight, "Pixel Map Renderer", 0, 0)
        if (window == 0L) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        glfwSetCursorPosCallback(window) { _, xpos, ypos ->
            mouseX = xpos
            mouseY = ypos
            translateCamera(xpos, ypos)
            lastMouseX = xpos
            lastMouseY = ypos
        }

        glfwSetMouseButtonCallback(window) { _, button, action, _ ->
            if (button == GLFW_MOUSE_BUTTON_RIGHT) isRightMouseDown = action == GLFW_PRESS
            if (button == GLFW_MOUSE_BUTTON_LEFT) isLeftMouseDown = action == GLFW_PRESS


            drawPixel()
        }

        glfwSetKeyCallback(window, object : GLFWKeyCallback() {
            override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
                if (key == GLFW_KEY_SPACE && action == GLFW_PRESS) {
                    if (!isSpacePressed) {
                        isSpacePressed = true
                    }
                } else if (key == GLFW_KEY_SPACE && action == GLFW_RELEASE) {
                    isSpacePressed = false
                }

                if (key == GLFW_KEY_LEFT_CONTROL && action == GLFW_PRESS) {
                    if (!isControlPressed) {
                        isControlPressed = true
                    }
                } else if (key == GLFW_KEY_LEFT_CONTROL && action == GLFW_RELEASE) {
                    isControlPressed = false
                }
            }
        })

        glfwSetScrollCallback(window) { _, _, yoffset ->
            val mod = yoffset * 0.1
            val newZoomLevel = zoomLevel + mod
            zoomLevel = if (newZoomLevel < 0.1) 0.1
            else if (newZoomLevel > 3) 3.0
            else {
                glTranslated(-offsetX, -offsetY, 0.0)
                glScaled(newZoomLevel / zoomLevel, newZoomLevel / zoomLevel, 1.0)
                glTranslated(offsetX, offsetY, 0.0)
                newZoomLevel
            }
        }

        glfwMakeContextCurrent(window)
        glfwSwapInterval(1)
        glfwShowWindow(window)

        GL.createCapabilities()

        glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3])
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        glOrtho(0.0, windowWidth.toDouble(), windowHeight.toDouble(), 0.0, -1.0, 1.0)

        glMatrixMode(GL_MODELVIEW)
        glLoadIdentity()
    }

    fun translateCamera(xpos: Double, ypos: Double) {

        if (isRightMouseDown) {
            val deltaX = (xpos - lastMouseX) / zoomLevel
            val deltaY = (ypos - lastMouseY) / zoomLevel
            offsetX += deltaX
            offsetY += deltaY
            glTranslated(deltaX, deltaY, 0.0)
        }
    }

    fun drawPixel() {
        if (isLeftMouseDown) {
            val pos = getMouseWorldPos()

            putPixel(pos.x, pos.y)
        }
    }

    fun getMouseWorldPos(): Point {
        val worldX = mouseX / zoomLevel - offsetX
        val worldY = mouseY / zoomLevel - offsetY

        val pixelX = (worldX / pixelSize).toInt()
        val pixelY = (worldY / pixelSize).toInt()
        return Point(pixelX, pixelY)
    }

    fun putPixel(x: Int, y: Int, value: Boolean? = null) {

        val booleans = pixelMap.getOrPut(x) { ConcurrentHashMap() }
        if (booleans.containsKey(y)) {
            if (value === null) {
                booleans[y] = !booleans[y]!!
            } else {
                booleans[y] = value
            }
        }
    }

    fun getPixel(x: Int, y: Int): Boolean? {
        if (x < 0 || y < 0 || x >= width || y >= height) return null
        val booleans = pixelMap.getOrPut(x) { ConcurrentHashMap() }
        return booleans[y] ?: false
    }

    private fun loop() {
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            handleInput()
            renderPixelMap()

            glfwSwapBuffers(window)
            glfwPollEvents()
        }
    }

    private fun handleInput() {
        // Обработка перемещения камеры левой кнопкой мыши
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS && !isRightMouseDown) {
            val deltaX = mouseX - lastMouseX
            val deltaY = mouseY - lastMouseY
            glTranslatef(deltaX.toFloat(), deltaY.toFloat(), 0.0f)
        }
    }

    private fun renderPixelMap() {


        val widthPixels = windowWidth / pixelSize / zoomLevel
        val heightPixels = windowHeight / pixelSize / zoomLevel

        val leftCorner = floor(-offsetX / pixelSize).toInt().coerceAtLeast(0)
        val topCorner = floor(-offsetY / pixelSize).toInt().coerceAtLeast(0)
        val rightCorner = floor(leftCorner + widthPixels).toInt().coerceAtMost(width)
        val bottomCorner = floor(topCorner + heightPixels).toInt().coerceAtMost(height)

        glColor4f(0f, 0f, 0f, 1.0f)

        loopIndicy(leftCorner,rightCorner) { x ->
            val ax = x * pixelSize

            loopIndicy (topCorner,bottomCorner) { y ->
                val ay = y * pixelSize
                glBegin(GL_LINES)
                glVertex2f(ax.toFloat(), ay.toFloat())
                glVertex2f((ax + pixelSize).toFloat(), ay.toFloat())
                glVertex2f((ax + pixelSize).toFloat(), ay.toFloat())
                glVertex2f((ax + pixelSize).toFloat(), (ay + pixelSize).toFloat())
                glVertex2f((ax + pixelSize).toFloat(), (ay + pixelSize).toFloat())
                glVertex2f(ax.toFloat(), (ay + pixelSize).toFloat())
                glVertex2f(ax.toFloat(), (ay + pixelSize).toFloat())
                glVertex2f(ax.toFloat(), ay.toFloat())
                glEnd()
            }
        }

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        loopIndicy(leftCorner,rightCorner) { x ->
            val ax = x * pixelSize

            val cols = pixelMap[x] ?: return@loopIndicy
            loopIndicy (topCorner,bottomCorner) { y ->
                val ay = y * pixelSize
                val value = cols[y] == true
                if (value) {
                    glBegin(GL_QUADS)
                    glVertex2f(ax.toFloat(), ay.toFloat())
                    glVertex2f((ax + pixelSize).toFloat(), ay.toFloat())
                    glVertex2f((ax + pixelSize).toFloat(), (ay + pixelSize).toFloat())
                    glVertex2f(ax.toFloat(), (ay + pixelSize).toFloat())
                    glEnd()
                }
            }
        }
    }

    private fun cleanup() {
        glfwFreeCallbacks(window)
        glfwDestroyWindow(window)
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }
}

fun main() {
    PixelMapRenderer(1280, 960, 400,400) {
        thread(true) {
            val game = GameOfLife(this)

            game.fillRandomly()

            fixedRateTimer("game", false, 0, 50) {
                if (isSpacePressed) return@fixedRateTimer
                game.evolve()
            }
        }
    }
}

class PixelFiller(val g: PixelMapRenderer) {

    fun startFill() {
        if (g.isSpacePressed) {
            val pos = g.getMouseWorldPos()
            fill(pos.x, pos.y)
        }
    }

    fun fill(x: Int, y: Int, call: Int = 0): Int {
        var newCall = call
        val color = g.getPixel(x, y) ?: return newCall
        if (newCall == 18) return newCall
        newCall++
        if (!color) {
            g.putPixel(x, y)
            newCall = fill(x - 1, y, newCall)
            newCall = fill(x + 1, y, newCall)
            newCall = fill(x, y - 1, newCall)
            newCall = fill(x, y + 1, newCall)
        }
        return newCall
    }
}

class GameOfLife(val g: PixelMapRenderer) {
    var grid
        get() = g.pixelMap
        set(value) {
            g.pixelMap = value
        }
    val rows get() = g.width
    val cols get() = g.height

    fun fillRandomly() {
        loopWhile(0,grid.size) { x ->
            val subMap = grid[x] ?: return@loopWhile
            loopWhile(0, subMap.size) { y ->
                subMap[y] = Random.nextInt(0,4) == 0
            }
        }
    }

    private fun countNeighbors(row: Int, col: Int): Int {
        var count = 0
        for (i in -1..1) {
            val newRow = row + i
            for (j in -1..1) {
                val newCol = col + j
                if (i != 0 || j != 0) {
                    if (newRow in 0..<rows && newCol in 0..<cols) {
                        if (grid[newRow]?.get(newCol) == true) {
                            count++
                        }
                    }
                }
            }
        }
        return count
    }

    fun evolve() {
        val newGrid = ConcurrentHashMap<Int, ConcurrentHashMap<Int, Boolean>>()

        for (i in 0..<rows) {
            val oldMap = grid.getOrPut(i) { ConcurrentHashMap<Int, Boolean>() }
            val newMap = newGrid.getOrPut(i) { ConcurrentHashMap<Int, Boolean>() }
            for (j in 0..<cols) {
                val neighbors = countNeighbors(i, j)
                val oldMapValue = oldMap[j] ?: false
                if (oldMapValue) {
                    // Cell is alive
                    newMap[j] = neighbors == 2 || neighbors == 3
                } else {
                    // Cell is dead
                    newMap[j] = neighbors == 3
                }
            }
        }

        grid.putAll(newGrid)
    }
}




