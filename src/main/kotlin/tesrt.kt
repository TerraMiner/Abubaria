import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.NativeHookException
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import java.awt.Robot
import java.awt.event.InputEvent
import java.util.*
import java.util.Timer
import javax.swing.*


fun main() {
    SwingUtilities.invokeLater { AutoClicker() }
}

class ControlPanel : JPanel() {
    val inputField = JTextField(10)
    val status = JLabel()
    val sendButton = JButton("Применить")
    val toggleButton = JButton("Start/Stop")
    val keyBoardListener = KeyBoardListener()

    var actionToggle = { }

    var value = 100

    init {
        add(inputField)
        add(sendButton)
        add(toggleButton)
        add(status)

        toggleButton.addActionListener { actionToggle() }
        setupInputField()
        setupKeyBoard()
    }

    fun setupInputField() {
        inputField.text = value.toString()
        sendButton.addActionListener {
            try {
                val text = inputField.text
                value = text.toInt()
            } catch (ex: NumberFormatException) {
                JOptionPane.showMessageDialog(this, "Ошибка! Введите корректное число.")
            }
        }
    }

    fun setupKeyBoard() {
        keyBoardListener.action = { actionToggle() }
        keyBoardListener.register()
    }

}

class KeyBoardListener : NativeKeyListener {
    var action = {}

    override fun nativeKeyPressed(e: NativeKeyEvent) {
    }

    override fun nativeKeyReleased(e: NativeKeyEvent) {
        if (e.keyCode == 64) {
            action()
        }
    }

    override fun nativeKeyTyped(e: NativeKeyEvent?) {}

    fun register() {
        try {
            GlobalScreen.registerNativeHook()
        } catch (ex: NativeHookException) {
            System.err.println("Failed to register native hook")
            ex.printStackTrace()
            System.exit(1)
        }

        GlobalScreen.addNativeKeyListener(this)
    }
}

class Clicker {
    private var robot = Robot()
    private var timer = Timer()
    private var isRunning = false

    var startAction = {}
    var stopAction = {}

    fun toggleAutoClicker(period: Int) {
        if (isRunning) {
            stopAutoClicker()
        } else {
            startAutoClicker(period)
        }
    }

    fun startAutoClicker(period: Int) {
        startAction()
        timer = Timer()
        isRunning = true
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                performClicks()
            }
        }, 0, period.toLong())
    }

    fun stopAutoClicker() {
        stopAction()
        isRunning = false
        timer.cancel()
    }

    private fun performClicks() {
        if (isRunning) {
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)

            robot.mousePress(InputEvent.BUTTON2_DOWN_MASK)
            robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK)

            robot.mousePress(InputEvent.BUTTON3_DOWN_MASK)
            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK)
        }
    }
}

class AutoClicker : JFrame() {

    val clicker = Clicker()
    val panel = ControlPanel()

    init {
        title = "Auto .Clicker"
        setSize(300, 200)
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)

        add(panel)

        clicker.stopAction = {
            panel.status.text = "Статус: Остановлен"
            panel.remove(panel.toggleButton)
            panel.remove(panel.status)
            panel.add(panel.status)
            panel.add(panel.toggleButton)
        }

        clicker.stopAction()

        clicker.startAction = {
            panel.status.text = "Статус: Запущен"
            panel.remove(panel.toggleButton)
            panel.remove(panel.status)
            panel.add(panel.toggleButton)
            panel.add(panel.status)
        }

        panel.actionToggle = {
            clicker.toggleAutoClicker(panel.value)
        }

        isVisible = true
    }

}