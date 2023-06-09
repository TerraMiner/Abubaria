package d2t.terra.abubaria

import DebugDisplay
import KeyHandler
import MouseHandler
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.hud.Hud
import d2t.terra.abubaria.world.World
import d2t.terra.abubaria.world.WorldGenerator
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import java.awt.*
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.JPanel
import kotlin.concurrent.thread
import org.lwjgl.stb.*
import java.nio.ByteBuffer


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

    val bgColor = Color(170, 255, 255)

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

    fun setupScreen() {

        val pos = getWindowPos(window)
        val size = getWindowSize(window)

        screenPosX = pos.first
        screenPosY = pos.second
        screenWidth2 = size.first
        screenHeight2 = size.second

        Camera.initialize()

    }

    fun startGameThread() {
        gameThread = thread(true) {
            tick()
        }

//        graphThread = thread(true) {
        draw()
//        }

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

            glEnable(GL_TEXTURE_2D)

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
        glfwTerminate()
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
//                world.update()

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
        val start = System.currentTimeMillis()
//        g2.color = bgColor
//        g2.fillRect(0, 0, screenWidth2, screenHeight2)
//        g2.color = Color.BLACK

        val loc = ClientPlayer.location.clone

//        kotlin.runCatching {

        glEnable(GL_BLEND)
        glBegin(GL_QUADS)

        world.draw(loc)

        Camera.draw(loc)

        Hud.draw()

        cursor.draw(loc)

        /*if (Client.debugMode)*/ display.text.apply {
            split("\n").forEachIndexed { index, text ->
                val y = index * 20 + 20f
//                    drawString(text, 4f, y, 5f,1f)
            }
        }

        val end = System.currentTimeMillis()

        videoLag = (end - start) / 1000.0
    }
}

