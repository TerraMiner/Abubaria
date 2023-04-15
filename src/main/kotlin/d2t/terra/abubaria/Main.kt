import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.GamePanel.inFullScreen
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

    window.title = "Abubaria"

    val cursorImg = BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)

    window.contentPane.cursor = Toolkit.getDefaultToolkit().createCustomCursor(
        cursorImg, Point(0, 0), "blank cursor"
    )

    window.add(GamePanel)

    window.pack()

    window.setLocationRelativeTo(null)

    window.isVisible = true

    GamePanel.setupScreen()

    window.addComponentListener(object : ComponentListener {
        override fun componentResized(e: ComponentEvent?) {
            GamePanel.setupScreen()
        }

        override fun componentMoved(e: ComponentEvent?) {
            GamePanel.setupScreen()
        }

        override fun componentShown(e: ComponentEvent?) {
            GamePanel.setupScreen()
        }

        override fun componentHidden(e: ComponentEvent?) {
            GamePanel.setupScreen()
        }

    })

    GamePanel.startGameThread()
}



