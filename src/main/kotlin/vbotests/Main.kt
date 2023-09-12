package vbotests

import d2t.terra.abubaria.window
import org.joml.Matrix4f
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.random.nextInt


fun main() {

    if (!GLFW.glfwInit()) {
        throw IllegalStateException("Failed to initialize GLFW")
    }

    window = glfwCreateWindow(640, 480, "Window", MemoryUtil.NULL, MemoryUtil.NULL)
    if (window == MemoryUtil.NULL) {
        throw RuntimeException("Failed to create the GLFW window")
    }

    GLFWErrorCallback.createPrint(System.err).set()

    glfwMakeContextCurrent(window)

    GL.createCapabilities()

    glClearColor(0f, 0f, 0f, 0f)

    glEnable(GL_TEXTURE_2D)

    val vertices = floatArrayOf(
        -.5f, .5f, 0f,//topleft
        .5f, .5f, 0f,//topright
        .5f, -.5f, 0f,//bottomright
        -.5f, -.5f, 0f,//bottomleft
    )

    val texture = floatArrayOf(
        0f, 0f,
        1f, 0f,
        1f, 1f,
        0f, 1f,
    )

    val indices = intArrayOf(
        0, 1, 2,
        2, 3, 0,
    )

    val model = Model(vertices, texture, indices)

    val shader = Shader("shader")

    val image = Texture("res/block/dirt.png")

    val projection = Matrix4f()
        .ortho2D(-640 / 2f, 640 / 2f, -480 / 2f, 480 / 2f)
        .scale(32f)

    thread(false) {
        var nextCheck = System.currentTimeMillis() + 50
        var angle = .0f
        while (!glfwWindowShouldClose(window)) {
            if (System.currentTimeMillis() > nextCheck) {
                nextCheck += 50
                angle += Random.nextDouble(-1.0,1.0).toFloat()

                projection
                    .translate(.01f, 0f, 0f)
                    .rotate(angle, 0f,0f,1f)

            }
        }
    }

    while (!glfwWindowShouldClose(window)) {
        glfwPollEvents()
        glClear(GL_COLOR_BUFFER_BIT)

        shader.bind()
        shader.setUniform("sampler", 0)
        shader.setUniform("projection", projection)

        image.bind()

        model.render()

        glfwSwapBuffers(window)
    }

    Callbacks.glfwFreeCallbacks(window)
    glfwSetErrorCallback(null)?.free()
    glfwSetWindowShouldClose(window, true)
    glfwTerminate()
    glfwDestroyWindow(window)
}

fun getWindowSize(window: Long): Pair<Int, Int> {
    val arrX = IntArray(1)
    val arrY = IntArray(1)
    glfwGetWindowSize(window, arrX, arrY)
    return arrX[0] to arrY[0]
}