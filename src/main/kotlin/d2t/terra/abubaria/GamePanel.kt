package d2t.terra.abubaria

import Cursor
import DebugDisplay
import KeyHandler
import MouseHandler
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.entity.player.Player
import d2t.terra.abubaria.world.World
import window
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.image.BufferedImage
import javax.swing.JPanel
import kotlin.concurrent.thread

object GamePanel : JPanel() {

    const val originalTileSize = 8

    val scale = 3

    var tileSize = originalTileSize * scale
    val maxScreenCol = 1600
    val maxScreenRow = 900
    val screenWidth = /*tileSize **/ maxScreenCol
    val screenHeight = /*tileSize **/ maxScreenRow

    var screenWidth2 = screenWidth
    var screenHeight2 = screenHeight

    var screenPosX = 0
    var screenPosY = 0

    lateinit var tempScreen: BufferedImage
    lateinit var g2: Graphics2D

    var physicsThread: Thread? = null
    var graphicsThread: Thread? = null

    val world = World().apply { generate() }

    val player = Player().apply { initialize() }

    val cursor = Cursor(0, 0)

    var videoLag = .0

    val display = DebugDisplay(player)

    var inFullScreen = false

    val bgColor = Color(170, 255, 255)

    init {
        preferredSize = Dimension(screenWidth2, screenHeight2)
        background = Color.BLACK
        isDoubleBuffered = true
        addKeyListener(KeyHandler)
        addMouseListener(MouseHandler)
        isFocusable = true
        Camera.initialize()
    }

    fun setupGame() {
        tempScreen = BufferedImage(screenWidth2, screenHeight2, BufferedImage.TYPE_INT_ARGB)
        g2 = tempScreen.graphics as Graphics2D
    }

    fun startGameThread() {

        val a = Any()

        physicsThread = thread(true) {
//            draw()

//            synchronized(a) {
            tick()
//            }
        }

//        synchronized(a) {
//            draw()
//        }
    }

    //    @Synchronized
    fun draw() {

        val tickInterval = 1000000000 / 10000.0

        var delta = .0
        var lastTime = System.nanoTime()
        var currentTime: Long

        var timer = 0L
        var drawCount = 0

        while (true) {
            currentTime = System.nanoTime()
            delta += (currentTime - lastTime) / tickInterval
            timer += (currentTime - lastTime)
            lastTime = currentTime

            if (delta >= 1) {
                drawToScreen()
                drawToTempScreen()
                delta--
                drawCount++
            }

            if (timer >= 1000000000) {
                display.fps = drawCount
                drawCount = 0
                timer = 0
            }
        }
    }

    fun tick() {
        val tickInterval = 1000000000 / 256.0

        var delta = 0.0
        var lastTime = System.nanoTime()
        var currentTime: Long

        var timer = 0L
        var drawCount = 0
        var tickCount = 0

        while (physicsThread != null) {
            currentTime = System.nanoTime()
            delta += (currentTime - lastTime) / tickInterval
            timer += (currentTime - lastTime)
            lastTime = currentTime

            while (delta >= 1.0) {
                cursor.update()
                player.update()
                delta -= 1.0
                tickCount++
            }

            drawToTempScreen()
            drawToScreen()

            drawCount++

            if (timer >= 1000000000) {
                display.fps = drawCount
                display.tps = tickCount
                drawCount = 0
                tickCount = 0
                timer = 0
            }
        }
    }

//    fun tick() {
//        val tickInterval = 1000000000 / 256.0
//
//        var delta = .0
//        var lastTime = System.nanoTime()
//        var currentTime: Long
//
//        var timer = 0L
//        var drawCount = 0
//
//        while (physicsThread != null) {
//            currentTime = System.nanoTime()
//            delta += (currentTime - lastTime) / tickInterval
//            timer += (currentTime - lastTime)
//            lastTime = currentTime
//
//            if (delta >= 1) {
//                cursor.update()
//                player.update()
//
//                drawToTempScreen()
//                drawToScreen()
//
//                delta = .0
//                drawCount++
//            }
//
//            if (timer >= 1000000000) {
//                display.tps = drawCount
//                drawCount = 0
//                timer = 0
//            }
//        }
//    }

    fun setFullScreen() {
        //
        inFullScreen = !inFullScreen

        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val gd = ge.defaultScreenDevice

        if (inFullScreen) {
            screenPosX = window.location.x
            screenPosY = window.location.y

            screenWidth2 = window.width
            screenHeight2 = window.height


            gd.fullScreenWindow = window
        } else {
            gd.fullScreenWindow = null

            window.setLocation(screenPosX, screenPosY)

            screenWidth2 = screenWidth
            screenHeight2 = screenHeight

            window.setSize(screenWidth2, screenHeight2)
        }

        Camera.initialize()
    }

    fun drawToScreen() {
        val g = graphics
        g.drawImage(tempScreen, 0, 0, screenWidth2, screenHeight2, null)
        g.dispose()
    }

    fun drawToTempScreen() {
        val start = System.currentTimeMillis()

        g2.color = bgColor
//        g2.clearRect(0, 0, window.width, window.height)
        g2.fillRect(0, 0, window.width, window.height)
        g2.color = Color.BLACK

//        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        world.draw(g2)

        Camera.draw(g2)

        cursor.draw(g2)

        if (Client.debugMode) display.text.apply {
            split("\n").forEachIndexed { index, text ->
                val y = index * 20 + 20
                g2.drawString(text, 4, y)
            }
        }

        val end = System.currentTimeMillis()

        videoLag = (end - start) / 1000.0

    }
}

