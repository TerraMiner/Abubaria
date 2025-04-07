package d2t.terra.abubaria

import d2t.terra.abubaria.world.Camera
import d2t.terra.abubaria.entity.impl.ClientPlayer
import d2t.terra.abubaria.event.EventHandler
import d2t.terra.abubaria.hud.Hud
import d2t.terra.abubaria.io.devices.KeyHandler
import d2t.terra.abubaria.io.devices.MouseHandler
import d2t.terra.abubaria.io.fonts.CFont
import d2t.terra.abubaria.io.graphics.Color
import d2t.terra.abubaria.io.graphics.Light
import d2t.terra.abubaria.io.graphics.LightRenderer
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.io.graphics.Window.windowId
import d2t.terra.abubaria.io.graphics.render.Layer
import d2t.terra.abubaria.io.graphics.render.RenderDimension
import d2t.terra.abubaria.io.graphics.render.Renderer
import d2t.terra.abubaria.util.TaskScheduler
import d2t.terra.abubaria.world.World
import d2t.terra.abubaria.world.generator.WorldGenerator
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import kotlin.concurrent.thread

object GamePanel {

    var gameThread: Thread? = null

    val world = World(32, 32)

    var videoLag = .0

    var inFullScreen = false

    var hasResized = false

    val font = CFont("fonts/Comic Sans MS.ttf", 64)

    private val bgColor = Color(150, 200, 250)

    private val lights = mutableListOf<Light>()

    fun startGame() {
        TaskScheduler.afterAsync(0) {
            WorldGenerator(world).generateWorld()
        }

        ClientPlayer.spawn()
        Camera.initialize()
        LightRenderer.init()

        EventHandler
        registerGameThread()

        // Добавляем несколько источников света с разными цветами
        lights.add(
            Light(
                x = 200f,
                y = 200f,
                radius = 2000f,
                falloff = 1f,
                color = Color.WHITE,
                intensity = 1f,
                flickerAmount = 0.0f,
                colorShift = .0f,
                penetrationMultiplier = 1f
            )
        )
        lights.add(
            Light(
                x = 200f,
                y = 500f,
                radius = 100f,
                falloff = 1.0f,
                color = Color.RED,
                intensity = 1f,
                flickerAmount = 0.0f,
                colorShift = .0f,
                penetrationMultiplier = 1f
            )
        )

        Window.startDrawLoop {
            TaskScheduler.tick()
            renderScreen()
        }
    }


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
                    world.tick()
                    Camera.tick()
                    update()
                    deltaTicks = .0
                    tickCount++
                }

                if (timer >= 1e9) {
                    DebugDisplay.tps = tickCount
                    tickCount = 0
                    timer = 0
                }
            }
        }
    }

    fun update() {
        // Обновляем позиции источников света (например, делаем их движущимися)
        val time = System.currentTimeMillis() / 1000.0
        lights[0].x = MouseHandler.x;//Window.centerX + (Math.sin(time) * 100).toFloat()
        lights[0].y = Window.height + -MouseHandler.y;//Window.centerY + (Math.cos(time) * 100).toFloat()
    }

    private fun renderScreen() {
        val start = System.currentTimeMillis()

        Camera.applyZoom()
        world.renderBlocks()

        if (Client.showWorldGrid) {
            Renderer.renderLine(
                0f,
                Window.centerY,
                Window.width.toFloat(),
                Window.centerY,
                color = Color.RED,
                layer = Layer.UI_DEBUG_LAYER,
                dim = RenderDimension.SCREEN
            )
            Renderer.renderLine(
                Window.centerX,
                0f,
                Window.centerX,
                Window.height.toFloat(),
                color = Color.GREEN,
                layer = Layer.UI_DEBUG_LAYER,
                dim = RenderDimension.SCREEN
            )
        }

        // Рендерим все батчи мира и сущностей
        LightRenderer.beginObstacleCapture()
        Renderer.render()
        LightRenderer.endObstacleCapture()

        world.renderBlocks()
        world.renderEntities()
        world.renderGrid()
        Renderer.render()

        // Рендерим освещение которое будет накладываться на мир
        LightRenderer.render(lights)

        // Рендерим UI выше чем свет чтобы он не применялся к UI
        Hud.render()
        DebugDisplay.render()
        Cursor.render()

        // Рендерим все батчи UI
        Renderer.render()

        val end = System.currentTimeMillis()
        videoLag = (end - start) / 1000.0
    }
}

