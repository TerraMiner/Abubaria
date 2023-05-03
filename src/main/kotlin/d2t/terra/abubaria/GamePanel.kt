package d2t.terra.abubaria

import DebugDisplay
import d2t.terra.abubaria.io.devices.KeyHandler
import LagDebugger
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.hud.Hud
import d2t.terra.abubaria.io.fonts.CFont
import d2t.terra.abubaria.world.World
import d2t.terra.abubaria.world.WorldGenerator
import lwjgl.drawString
import lwjgl.drawTexture
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import java.awt.Color
import java.awt.Image
import kotlin.concurrent.thread


object GamePanel {

    private const val originalTileSize = 8

    private const val scale = 3

    var tileSize = originalTileSize * scale
    val maxScreenCol = 1280
    val maxScreenRow = 720
    val screenWidth = maxScreenCol
    val screenHeight = maxScreenRow

    var screenWidth2 = screenWidth
    var screenHeight2 = screenHeight

    var screenPosX = 0
    var screenPosY = 0

    var gameThread: Thread? = null

    val world = World().apply { /*generate()*/
        WorldGenerator(this).generateWorld()
    }

    val cursor = Cursor(0, 0)

    var videoLag = .0

    val display = DebugDisplay()

    var inFullScreen = false

    var hasResized = false

    val font = CFont("fonts/Comic Sans MS.ttf","Comic Sans MS",64)

    init {

        Camera.initialize()
    }

    fun getWindowPos(window: Long): Pair<Int, Int> {
        val arrX = IntArray(1)
        val arrY = IntArray(1)
        glfwGetWindowPos(window, arrX, arrY)
        return arrX[0] to arrY[0]
    }

    fun getWindowSize(window: Long): Pair<Int, Int> {
        val arrX = IntArray(1)
        val arrY = IntArray(1)
        glfwGetWindowSize(window, arrX, arrY)
        return arrX[0] to arrY[0]
    }

//    private val bgColor = Color(170, 255, 255)
private val bgColor = Color(40, 40, 50)

    fun setupScreen() {

        val pos = getWindowPos(window)
        val size = getWindowSize(window)

        screenPosX = pos.first
        screenPosY = pos.second
        screenWidth2 = size.first
        screenHeight2 = size.second

        glClearColor(bgColor.red / 255f, bgColor.green / 255f, bgColor.blue / 255f, bgColor.alpha / 255f)

        glViewport(0, 0, screenWidth2, screenHeight2)

        glMatrixMode(GL_PROJECTION)
        glEnable(GL_TEXTURE_2D)

        glLoadIdentity()
        glOrtho(0.0, screenWidth2.toDouble(), screenHeight2.toDouble(), 0.0, 0.0, 1.0)
        glMatrixMode(GL_MODELVIEW)

        Camera.initialize()

    }

    fun startGameThread() {
        gameThread = thread(true) {
            tick()
        }
        draw()
    }


    private fun draw() {

        var lastTime = System.nanoTime()
        var currentTime: Long
        var timer = 0L
        var drawCount = 0


        while (!glfwWindowShouldClose(window)) {

            currentTime = System.nanoTime()
            timer += (currentTime - lastTime)
            lastTime = currentTime

            glClear(GL_COLOR_BUFFER_BIT)

            drawToTempScreen()

            glfwSwapBuffers(window)

            glfwPollEvents()

            drawCount++

            if (timer >= 1000000000) {
                display.fps = drawCount
                drawCount = 0
                timer = 0
            }
        }

        glfwDestroyWindow(window)
    }


    private fun tick() {
        val tickInterval = 1e9 / 256.0
        var deltaTicks = .0
        var lastTime = System.nanoTime()
        var currentTime: Long
        var timer = 0L
        var tickCount = 0
        while (!glfwWindowShouldClose(window)) {
            currentTime = System.nanoTime()
            deltaTicks += (currentTime - lastTime) / tickInterval
            timer += (currentTime - lastTime)
            lastTime = currentTime
            while (deltaTicks >= 1.0) {
                Camera.interpolate()
                ClientPlayer.update()
                cursor.update()
                KeyHandler.update()
                world.update()

                deltaTicks -= 1.0
                tickCount++
            }

            if (timer >= 1000000000) {
                display.tps = tickCount
                tickCount = 0
                timer = 0
            }
        }
    }


    private fun drawToTempScreen() {
        val a = LagDebugger()
        a.enabled = false
        a.check(173)
        val start = System.currentTimeMillis()
        val loc = ClientPlayer.location.clone

        glEnable(GL_BLEND)
        a.check(182)

        world.draw(loc)
        a.check(185)

        Camera.draw(loc)
        a.check(188)

        Hud.draw()
        a.check(191)

        cursor.draw(loc)
        a.check(194)


        if (Client.debugMode) display.text.apply {
            split("\n").forEachIndexed { index, text ->
                val y = index * 20 + 20
                    drawString(text, 4, y, 5,Color.DARK_GRAY)
            }
        }

        val end = System.currentTimeMillis()

        videoLag = (end - start) / 1000.0
        a.check(207)
        a.debug("Video lag")
    }
}

