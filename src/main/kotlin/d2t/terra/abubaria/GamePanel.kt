package d2t.terra.abubaria

import d2t.terra.abubaria.world.Camera
import d2t.terra.abubaria.entity.impl.ClientPlayer
import d2t.terra.abubaria.event.EventHandler
import d2t.terra.abubaria.hud.Hud
import d2t.terra.abubaria.io.devices.KeyHandler
import d2t.terra.abubaria.io.devices.MouseHandler
import d2t.terra.abubaria.io.fonts.CFont
import d2t.terra.abubaria.io.fonts.TextHorAligment
import d2t.terra.abubaria.io.fonts.TextHorPosition
import d2t.terra.abubaria.io.fonts.TextVerAlignment
import d2t.terra.abubaria.io.fonts.TextVerPosition
import d2t.terra.abubaria.io.graphics.Color
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.io.graphics.Window.windowId
import d2t.terra.abubaria.io.graphics.render.RenderDimension
import d2t.terra.abubaria.io.graphics.render.Renderer
import d2t.terra.abubaria.io.graphics.render.UI_DEBUG_LAYER
import d2t.terra.abubaria.io.graphics.render.WORLD_ENTITY_LAYER
import d2t.terra.abubaria.util.TaskScheduler
import d2t.terra.abubaria.util.print
import d2t.terra.abubaria.world.World
import d2t.terra.abubaria.world.generator.WorldGenerator
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import org.lwjgl.opengl.GL11.GL_BLEND
import org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA
import org.lwjgl.opengl.GL11.GL_SRC_ALPHA
import org.lwjgl.opengl.GL11.glBlendFunc
import org.lwjgl.opengl.GL11.glEnable
import kotlin.concurrent.thread

object GamePanel {

    var gameThread: Thread? = null

    val world = World()

    var videoLag = .0

    var partialTick: Float = 0f

    val display = DebugDisplay()

    var inFullScreen = false

    var hasResized = false

    val font = CFont("fonts/Comic Sans MS.ttf", 64)
//    val font = CFont("fonts/PhantomMuff 1.5 Plus Regular.ttf", 64)
//    val font = CFont("fonts/alagard-12px-unicode.ttf", 64)
//    val font = CFont("fonts/Intro.otf", 64)

    private val bgColor = Color(150, 200, 250)

    fun startGame() {
//        lightThread = thread(true, false, null, "lightThread") {
//            LightManager.tick()
//        }
        TaskScheduler.afterAsync(0) {
            WorldGenerator(world).generateWorld()
//            world.generateWorldLight()
        }

        ClientPlayer.spawn()
        Camera.initialize()

        EventHandler
        registerGameThread()

        Window.startDrawLoop {
            TaskScheduler.tick()
            drawScreen()
        }
    }

    var step = 0
    val maxStep = tickrate * 3

    private fun registerGameThread() {
        gameThread = thread(true, false, null, "gameThread") {
            val tickInterval = 1e9 / tickrate
            var deltaTicks = .0
            var lastTime = System.nanoTime()
            var currentTime: Long
            var timer = 0L
            var tickCount = 0
            while (!glfwWindowShouldClose(windowId)) {
                currentTime = System.nanoTime()
                deltaTicks += (currentTime - lastTime) / tickInterval
                timer += (currentTime - lastTime)
                lastTime = currentTime
                while (deltaTicks >= 1.0) {
                    KeyHandler.update()
                    MouseHandler.update()
                    Cursor.update()
                    Camera.coerceInWorld()
                    world.tick()

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
    }


    var debug = true
    var positionX = TextHorPosition.CENTER
    var alignX = TextHorAligment.CENTER
    var positionY = TextVerPosition.CENTER
    var alignY = TextVerAlignment.BOTTOM

    private fun drawScreen() {
        val start = System.currentTimeMillis()

        Camera.interpolate()
        Camera.applyZoom()

        Renderer.renderText(
            "Привет!\nAbubaria",
            world.spawnLocation.x.toFloat(),
            world.spawnLocation.y.toFloat(),
            64,
            color = Color.gradientRainbow(step, maxStep),
            textHorAligment = alignX,
            textHorPosition = positionX,
            textVerAlignment = alignY,
            textVerPosition = positionY,
            zIndex = WORLD_ENTITY_LAYER,
            dim = RenderDimension.WORLD,
            ignoreCamera = false
        )

        if (Client.debugMode) {
            Renderer.renderLine(0f, Window.centerY, Window.width.toFloat(), Window.centerY, color = Color.RED, zIndex = UI_DEBUG_LAYER, dim = RenderDimension.SCREEN)
            Renderer.renderLine(Window.centerX, 0f, Window.centerX, Window.height.toFloat(), color = Color.GREEN, zIndex = UI_DEBUG_LAYER, dim = RenderDimension.SCREEN)
        }


        world.draw()

        Hud.draw()
        display.draw()
        Cursor.draw()

        Renderer.render()
//
//        val text = font.characterMap.keys.map(::Char).chunked(font.atlasSquareSize).map { it.joinToString("  ") }
//            .joinToString("\n\n")
//        RendererManager.WorldRenderer.renderText(
//            text,
//            world.spawnLocation.x.toFloat(),
//            world.spawnLocation.y.toFloat(),
//            .5f,
//            textHorPosition = TextHorPosition.CENTER
//        )

//        RendererManager.WorldRenderer.renderText(
//            "Съешьте ещё этих мягких французских булок, да выпейте же чаю!  \n" +
//                    "СЪЕШЬТЕ ЕЩЁ ЭТИХ МЯГКИХ ФРАНЦУЗСКИХ БУЛОК, ДА ВЫПЕЙТЕ ЖЕ ЧАЮ!  \n" +
//                    "The quick brown fox jumps over the lazy dog.  \n" +
//                    "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG.  \n" +
//                    "#@\$%^&*()_+-=[]{};:'\",.<>/?",
//            20f, 80f, .5f
//        )

        val end = System.currentTimeMillis()
        videoLag = (end - start) / 1000.0
    }
}

