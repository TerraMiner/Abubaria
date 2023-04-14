import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.entity.player.Camera
import java.awt.Dimension
import java.awt.Point
import java.awt.Toolkit
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import java.awt.image.BufferedImage
import javax.swing.JFrame

val window = JFrame()


//fun main() {}

fun main() {
    window.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

    window.title = "Game"

    val cursorImg = BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)

    window.contentPane.cursor = Toolkit.getDefaultToolkit().createCustomCursor(
        cursorImg, Point(0, 0), "blank cursor"
    )

    window.add(GamePanel)

    window.pack()

    window.setLocationRelativeTo(null)

    window.isVisible = true

    GamePanel.setupGame()

    window.addComponentListener(object : ComponentListener {
        override fun componentResized(e: ComponentEvent?) {
            GamePanel.screenWidth2 = window.rootPane.width
            GamePanel.screenHeight2 = window.rootPane.height
            GamePanel.preferredSize = Dimension(window.width,window.height)
            GamePanel.tempScreen = BufferedImage(GamePanel.screenWidth2, GamePanel.screenHeight2,BufferedImage.TYPE_INT_ARGB)
            GamePanel.g2 = GamePanel.tempScreen.createGraphics()
            Camera.initialize()
        }

        override fun componentMoved(e: ComponentEvent?) {

        }

        override fun componentShown(e: ComponentEvent?) {

        }

        override fun componentHidden(e: ComponentEvent?) {

        }

    })



    GamePanel.startGameThread()
}



