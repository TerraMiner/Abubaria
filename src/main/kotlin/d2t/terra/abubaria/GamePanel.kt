package d2t.terra.abubaria

import DebugDisplay
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.event.BlockDestroyEvent
import d2t.terra.abubaria.event.EventHandler
import d2t.terra.abubaria.event.EventService
import d2t.terra.abubaria.hud.Hud
import d2t.terra.abubaria.io.LagDebugger
import d2t.terra.abubaria.io.devices.KeyHandler
import d2t.terra.abubaria.io.fonts.CFont
import d2t.terra.abubaria.light.LightManager
import d2t.terra.abubaria.world.World
import d2t.terra.abubaria.world.WorldGenerator
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import kotlin.concurrent.thread


object GamePanel {

    private const val originalTileSize = 8

    private const val scale = 2

    const val tileSize = originalTileSize * scale

    val defaultScreenPosX = 1
    val defaultScreenPosY = 1
    val defaultScreenWidth = windowWidth
    val defaultScreenHeight = windowHeight

    var screenPosX = defaultScreenPosX
    var screenPosY = defaultScreenPosY
    var screenWidth = defaultScreenWidth
    var screenHeight = defaultScreenHeight

    private var gameThread: Thread? = null
    private var chatThread: Thread? = null

    val world = World().apply { /*generate()*/
        WorldGenerator(this).generateWorld()
//        val x = (worldWidth / tileSize) / 2
//        val y = 20
//        setBlock(Material.STONE, x, y)
    }

    val cursor = Cursor(0, 0)

    var videoLag = .0

    val display = DebugDisplay()

    var inFullScreen = false

    var hasResized = false

    val font = CFont("fonts/Comic Sans MS.ttf", "Comic Sans MS", 64)

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

        val size = getWindowSize(window)

        screenWidth = size.first
        screenHeight = size.second

        glClearColor(bgColor.red / 255f, bgColor.green / 255f, bgColor.blue / 255f, bgColor.alpha / 255f)

        glViewport(0, 0, screenWidth, screenHeight)

        glMatrixMode(GL_PROJECTION)
        glEnable(GL_TEXTURE_2D)

        glLoadIdentity()
        glOrtho(0.0, screenWidth.toDouble(), screenHeight.toDouble(), 0.0, 0.0, 1.0)
        glMatrixMode(GL_MODELVIEW)

        Camera.initialize()
    }

    fun startGameThread() {
        ClientPlayer.initialize()

        world.generateWorldLight()

        EventHandler

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

//        glEnable(GL_BLEND)

        while (!glfwWindowShouldClose(window)) {

            currentTime = System.nanoTime()
            timer += (currentTime - lastTime)
            lastTime = currentTime

            glfwPollEvents()

            glClear(GL_COLOR_BUFFER_BIT)

            Camera.interpolate()

            drawScreen()

            glfwSwapBuffers(window)

            drawCount++

            if (timer >= 1e9) {
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
                ClientPlayer.update()
                cursor.update()
                KeyHandler.update()
                world.update()

                deltaTicks = .0
                tickCount++
            }

            if (timer >= 1000000000) {
                display.tps = tickCount
                tickCount = 0
                timer = 0
            }
        }
    }

    private fun drawScreen() {
        val a = LagDebugger()
        a.enabled = false
        val start = System.currentTimeMillis()
        val loc = ClientPlayer.location.clone
        a.check(180)
        world.draw(loc)//LAGS 1.8386
        a.check(182)
        Camera.draw(loc)//LAGS 0.0021
        a.check(184)
        LightManager.draw(loc)//LAGS 1.8529
        a.check(186)
        Hud.draw()//LAGS 0.0143
        a.check(188)
        cursor.draw(loc)//0.0025
        a.check(190)
        display.draw()//LAGS 0.0106
        a.check(192)
        val end = System.currentTimeMillis()
        videoLag = (end - start) / 1000.0
        a.check(195)
        a.debug("drawScreen")
    }
}

