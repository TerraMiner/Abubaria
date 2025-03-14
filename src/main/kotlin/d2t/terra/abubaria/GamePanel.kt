package d2t.terra.abubaria

import d2t.terra.abubaria.entity.player.Camera
import d2t.terra.abubaria.entity.player.ClientPlayer
import d2t.terra.abubaria.event.EventHandler
import d2t.terra.abubaria.hud.Hud
import d2t.terra.abubaria.io.devices.KeyHandler
import d2t.terra.abubaria.io.fonts.CFont
import d2t.terra.abubaria.io.fonts.TextHorAligment
import d2t.terra.abubaria.io.fonts.TextHorPosition
import d2t.terra.abubaria.io.fonts.TextVerAlignment
import d2t.terra.abubaria.io.fonts.TextVerPosition
import d2t.terra.abubaria.io.graphics.Color
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.io.graphics.render.RendererManager
import d2t.terra.abubaria.world.World
import d2t.terra.abubaria.world.WorldGenerator
import java.util.concurrent.Executors
import kotlin.concurrent.thread


object GamePanel {

    private const val originalTileSize = 8

    private const val scale = 2

    const val tileSize = originalTileSize * scale
    const val tileSizeF = originalTileSize.toFloat() * scale.toFloat()

    var gameThread: Thread? = null
    var lightThread: Thread? = null
    private var chatThread: Thread? = null
    val service = Executors.newFixedThreadPool(4)

    val world = World()

    val cursor = Cursor(0, 0)

    var videoLag = .0

    val display = DebugDisplay()

    var inFullScreen = false

    var hasResized = false

    val font = CFont("fonts/Comic Sans MS.ttf", "Comic Sans MS", 64)

    private val bgColor = Color(150, 200, 250)

    fun startGameThread() {
        ClientPlayer.initialize()

//        lightThread = thread(true, false, null, "lightThread") {
//            LightManager.tick()
//        }
        service.submit {
            WorldGenerator(world).generateWorld()
//            world.generateWorldLight()
        }

        EventHandler

        gameThread = thread(true, false, null, "gameThread") {
            tick()
        }

        Window.draw {
            Camera.interpolate(ClientPlayer.location)
            drawScreen()
        }
    }

    var step = 0
    val maxStep = 256 * 3

    private fun tick() {
        val tickInterval = 1e9 / 256.0
        var deltaTicks = .0
        var lastTime = System.nanoTime()
        var currentTime: Long
        var timer = 0L
        var tickCount = 0
        Window.whileWindowOpened {
            currentTime = System.nanoTime()
            deltaTicks += (currentTime - lastTime) / tickInterval
            timer += (currentTime - lastTime)
            lastTime = currentTime
            while (deltaTicks >= 1.0) {
                ClientPlayer.update()
                KeyHandler.update()
                cursor.update()
                world.update()

                if (++step > maxStep) step = 0

                deltaTicks = .0
                tickCount++
            }

            if (timer >= 1e9) {
                display.tps = tickCount
                tickCount = 0
                timer = 0
            }
        }
    }

    var debug = true
    var positionX = TextHorPosition.CENTER
    var alignX = TextHorAligment.CENTER
    var positionY = TextVerPosition.CENTER
    var alignY = TextVerAlignment.CENTER

    private fun drawScreen() {
        val start = System.currentTimeMillis()
        val loc = ClientPlayer.location.clone

        val shader = RendererManager.WorldRenderer.shader
        shader.performSnapshot(shader.colorPalette) {
            RendererManager.WorldRenderer.renderText(
                "Привет!\nAbubaria",
                world.spawnLocation.x,
                world.spawnLocation.y,
                1f,
                color = Color.gradientRainbow(step, maxStep),
                textHorAligment = alignX,
                textHorPosition = positionX,
                textVerAlignment = alignY,
                textVerPosition = positionY,
            )
        }

        world.draw()
        Camera.draw(loc)
        Hud.draw()
        cursor.draw(loc)
        display.draw()

        val end = System.currentTimeMillis()
        videoLag = (end - start) / 1000.0
    }
}

