package d2t.terra.abubaria

import DebugDisplay
import KeyHandler
import MouseHandler
import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.hud.Hud
import d2t.terra.abubaria.world.World
import d2t.terra.abubaria.world.WorldGenerator
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.JPanel
import kotlin.concurrent.thread


object GamePanel : JPanel() {

    const val originalTileSize = 8

    val scale = 3

    var tileSize = originalTileSize * scale
    val maxScreenCol = 1280
    val maxScreenRow = 720
    val screenWidth = /*tileSize **/ maxScreenCol
    val screenHeight = /*tileSize **/ maxScreenRow

    var screenWidth2 = screenWidth
    var screenHeight2 = screenHeight

    var screenPosX = 0
    var screenPosY = 0

    lateinit var tempScreen: BufferedImage
    lateinit var g2: Graphics2D

    val ge: GraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
    val gd: GraphicsDevice = ge.defaultScreenDevice

    var gameThread: Thread? = null
    var graphThread: Thread? = null

    val world = World().apply { /*generate()*/
        WorldGenerator(this).generateWorld()
    }

    val cursor = Cursor(0, 0)

    var videoLag = .0

    val display = DebugDisplay()

    var inFullScreen = false

    val bgColor = Color(170, 255, 255)

    lateinit var defaultGameFont: Font

    init {
        preferredSize = Dimension(screenWidth2, screenHeight2)
        background = Color.BLACK
        isDoubleBuffered = true
        addKeyListener(KeyHandler)
        addMouseListener(MouseHandler)
        addMouseWheelListener(MouseHandler)
        isFocusable = true
        Camera.initialize()
    }

    fun setupScreen() {

        screenPosX = window.location.x
        screenPosY = window.location.y
        screenWidth2 = window.rootPane.width
        screenHeight2 = window.rootPane.height
        GamePanel.preferredSize = Dimension(window.width, window.height)

        tempScreen = BufferedImage(screenWidth2, screenHeight2, BufferedImage.TYPE_INT_ARGB)
        g2 = tempScreen.createGraphics()

        val hints = RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        hints.add(RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY))
        hints.add(RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON))
        hints.add(RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY))
        g2.setRenderingHints(hints)

        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, File("fonts/Comic Sans MS.ttf")))
        defaultGameFont = Font("Comic Sans MS", Font.PLAIN, 16)
        g2.font = defaultGameFont

        Camera.initialize()

    }

    fun startGameThread() {
        gameThread = thread(true) {
            tick()
        }

        graphThread = thread(true) {

            draw()
        }

    }


    fun draw() {
        var lastTime = System.nanoTime()
        var currentTime: Long
        var timer = 0L
        var drawCount = 0

        while (graphThread != null) {
            currentTime = System.nanoTime()
            timer += (currentTime - lastTime)
            lastTime = currentTime

            cursor.update()
            drawToTempScreen()
            drawToScreen()

            drawCount++

            if (timer >= 1000000000) {
                display.fps = drawCount
                drawCount = 0
                timer = 0
            }
        }
    }


    fun tick() {
        val tickInterval = 1e9 / 256.0
        var deltaTicks = .0
        var lastTime = System.nanoTime()
        var currentTime: Long
        var timer = 0L
        var tickCount = 0
        while (gameThread != null) {
            currentTime = System.nanoTime()
            deltaTicks += (currentTime - lastTime) / tickInterval
            timer += (currentTime - lastTime)
            lastTime = currentTime
            while (deltaTicks >= 1.0) {
                Camera.interpolate()
                ClientPlayer.update()
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

    fun setFullScreen(screen: Boolean) {
        //
        inFullScreen = screen

        if (inFullScreen) {
            screenPosX = window.location.x
            screenPosY = window.location.y

            screenWidth2 = window.width
            screenHeight2 = window.height

            window.setSize(screenWidth2, screenHeight2)
            gd.fullScreenWindow = window

        } else {
            gd.fullScreenWindow = null

            window.setLocation(screenPosX, screenPosY)

            screenWidth2 = screenWidth
            screenHeight2 = screenHeight

            window.setSize(screenWidth2, screenHeight2)

        }
    }

    fun drawToScreen() {
        val g = graphics
        g.drawImage(tempScreen, 0, 0, null)
        g.dispose()
    }

    fun drawToTempScreen() {
        val start = System.currentTimeMillis()
        g2.color = bgColor
        g2.fillRect(0, 0, screenWidth2, screenHeight2)
        g2.color = Color.BLACK

        val loc = ClientPlayer.location.clone

        kotlin.runCatching {
            world.draw(g2, loc)

            Camera.draw(g2, loc)

            Hud.draw(g2)

            cursor.draw(g2, loc)

            if (Client.debugMode) display.text.apply {
                val oldc = g2.color
                g2.color = Color.GRAY
                split("\n").forEachIndexed { index, text ->
                    val y = index * 20 + 20
                    g2.drawString(text, 4, y)
                }
                g2.color = oldc
            }
        }.getOrElse {
            println(it.message)
        }

        val end = System.currentTimeMillis()

        videoLag = (end - start) / 1000.0
    }
}

