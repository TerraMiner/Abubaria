package lwjgl

import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.ByteBuffer
import java.nio.IntBuffer


//fun main() {
//    val width = 640
//    val height = 480
//
//    if (!glfwInit()) {
//        throw IllegalStateException("Failed to initialize GLFW")
//    }
//
//    val window = glfwCreateWindow(width, height, "Abubaria", NULL, NULL)
//    if (window == NULL) {
//        throw RuntimeException("Failed to create the GLFW window")
//    }
//
//    glfwMakeContextCurrent(window)
//
//    GL.createCapabilities()
//
//    glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
//
//    glViewport(0, 0, width, height)
//
//    glMatrixMode(GL_PROJECTION)
//    glLoadIdentity()
//    glOrtho(0.0, width.toDouble(), height.toDouble(), 0.0, 0.0, 1.0)
//    glMatrixMode(GL_MODELVIEW)
//
//    while (!glfwWindowShouldClose(window)) {
//        glClear(GL_COLOR_BUFFER_BIT)
//
//        glEnable(GL_TEXTURE_2D)
//
//        drawTexture("res/block/grass.png",0,0,32,32)
//
//        glfwSwapBuffers(window)
//        glfwPollEvents()
//    }
//
//    glfwDestroyWindow(window)
//    glfwTerminate()
//}



fun loadImage(texturePath: String): Image {
//    return stbi_load(texturePath, imageWidth, imageHeight, imageChannels, STBI_rgb_alpha)
    return Image(texturePath)
}

class Image(texturePath: String) {

    private var imageWidth = IntArray(1)
    private var imageHeight = IntArray(1)
    private var imageChannels = IntArray(1)

    var width = 0
    var height = 0
    var channel = 0

    var image: ByteBuffer? = null
    init {
        image = stbi_load(texturePath, imageWidth, imageHeight, imageChannels, STBI_rgb_alpha)
        width = imageWidth[0]
        height = imageHeight[0]
        channel = imageChannels[0]
    }
}

fun drawTexture(image: Image?, x: Int, y: Int, width: Int, height: Int) {
    if (image?.image == null) return
    val textureId = glGenTextures()
    glBindTexture(GL_TEXTURE_2D, textureId)

    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.width, image.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image.image)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

    glEnable(GL_TEXTURE_2D)
    glBindTexture(GL_TEXTURE_2D, textureId)

    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

    glBegin(GL_QUADS)

    glTexCoord2f(0.0f, 1.0f)
    glVertex2i(x, y + height)

    glTexCoord2f(1.0f, 1.0f)
    glVertex2i(x + width, y + height)

    glTexCoord2f(1.0f, 0.0f)
    glVertex2i(x + width, y)

    glTexCoord2f(0.0f, 0.0f)
    glVertex2i(x, y)

    glEnd()

    glDeleteTextures(textureId)
}
