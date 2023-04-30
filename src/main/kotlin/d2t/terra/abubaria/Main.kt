package d2t.terra.abubaria

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL

var window: Long = 0

fun main() {
    val width = 640
    val height = 480

    if (!glfwInit()) {
        throw IllegalStateException("Failed to initialize GLFW")
    }

    window = glfwCreateWindow(width, height, "Abubaria", NULL, NULL)
    if (window == NULL) {
        throw RuntimeException("Failed to create the GLFW window")
    }

    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN)

    glfwMakeContextCurrent(window)

    GL.createCapabilities()

    glClearColor(0.5f, 0.5f, 1.0f, 0.0f)

    glViewport(0, 0, width, height)

    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    glOrtho(0.0, width.toDouble(), height.toDouble(), 0.0, 0.0, 1.0)
    glMatrixMode(GL_MODELVIEW)

    GamePanel.setupScreen()

    GamePanel.startGameThread()

//    glfwDestroyWindow(window)
//    glfwTerminate()
}

fun main1() {
//    window.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
//
//    window.title = "Abubaria"

//    val cursorImg = BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)

//    window.contentPane.cursor = Toolkit.getDefaultToolkit().createCustomCursor(
//        cursorImg, Point(0, 0), "blank cursor"
//    )

//    window.add(GamePanel)

//    window.pack()

//    window.setLocationRelativeTo(null)

//    window.isVisible = true

//    GamePanel.setupScreen()

//    window.addComponentListener(object : ComponentListener {
//        override fun componentResized(e: ComponentEvent?) {
//            GamePanel.setupScreen()
//        }

//        override fun componentMoved(e: ComponentEvent?) {
//            GamePanel.setupScreen()
//        }

//        override fun componentShown(e: ComponentEvent?) {
//            GamePanel.setupScreen()
//        }

//        override fun componentHidden(e: ComponentEvent?) {
//            GamePanel.setupScreen()
//        }

//    })

    GamePanel.startGameThread()
}



