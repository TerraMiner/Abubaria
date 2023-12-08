package vbotests.game

import d2t.terra.abubaria.io.LagDebugger
import org.joml.Vector3f
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import vbotests.render.Camera
import vbotests.render.Shader
import vbotests.io.Window
import vbotests.world.Tile
import vbotests.world.TileRenderer
import vbotests.world.World
import java.lang.ref.Cleaner

val cleaner = Cleaner.create()
fun main() {
    
    Window.setCallBacks()
    if (!glfwInit()) {
        throw IllegalStateException("Failed to initialize GLFW")
    }

    GLFWErrorCallback.createPrint(System.err).set()

    val window = Window("Game",1280,960)
//    window.setFullScreen(true)
    window.createWindow()

    GL.createCapabilities()

    glClearColor(0f, 0f, 0f, 0f)

    val camera = Camera(window.width, window.height)

    glEnable(GL_TEXTURE_2D)

    val shader = Shader("shader")
    TileRenderer
    val world = World(1024,1024)
    world.generate()

    world.setTile(Tile.tiles[2]!!,1,1)
    world.setTile(Tile.NullTile,2,2)
    world.setTile(Tile.tiles[2]!!,63,63)

    val fpsLimit = 9999

    val frameCap = 1.0 / fpsLimit

    var frameTime = .0
    var frames = 0

    var time = CurrentTime
    var unprocessed = .0

    val speed = 0.01f

    shader.bind()
    shader.setSamplerUniform(0)

    while (!window.shouldClose()) {

        var canRender = false

        val time2 = CurrentTime
        val passed = time2 - time
        unprocessed += passed
        frameTime += passed

        time = time2
        while (unprocessed >= frameCap) {
            unprocessed -= frameCap
            canRender = true

            if (window.input.isKeyPressed(GLFW_KEY_ESCAPE)) {
                glfwSetWindowShouldClose(window.id, true)
            }
            if (window.input.isKeyDown(GLFW_KEY_A)) {
                camera.position.sub(Vector3f(-speed,0f,0f))
            }

            if (window.input.isKeyDown(GLFW_KEY_D)) {
                camera.position.sub(Vector3f(speed,0f,0f))
            }

            if (window.input.isKeyDown(GLFW_KEY_S)) {
                camera.position.sub(Vector3f(0f,speed,0f))
            }

            if (window.input.isKeyDown(GLFW_KEY_W)) {
                camera.position.sub(Vector3f(0f,-speed,0f))
            }

            world.correctCamera(camera,window)

            window.update()

            if (frameTime >= 1.0) {
                frameTime = .0
                println("FPS: $frames")
                frames = 0
            }
        }

        if (canRender) {

            glClear(GL_COLOR_BUFFER_BIT)

            world.render(shader, camera, window)

            window.swapBuffers()
            frames++
        }
    }

    Callbacks.glfwFreeCallbacks(window.id)
    glfwSetErrorCallback(null)?.free()
    glfwSetWindowShouldClose(window.id, true)
    glfwTerminate()
    glfwDestroyWindow(window.id)
}

fun getWindowSize(window: Long): Pair<Int, Int> {
    val arrX = IntArray(1)
    val arrY = IntArray(1)
    glfwGetWindowSize(window, arrX, arrY)
    return arrX[0] to arrY[0]
}

val CurrentTime get() = System.nanoTime().toDouble() / 1000000000.0