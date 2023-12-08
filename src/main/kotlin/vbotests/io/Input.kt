package vbotests.io

import org.lwjgl.glfw.GLFW.*

class Input(private val window: Long) {

    val keys = arrayOfNulls<Boolean>(GLFW_KEY_LAST)

    init {
        for (i in 32..<GLFW_KEY_LAST) {
            keys[i] = false
        }
    }

    fun isKeyDown(key: Int) = glfwGetKey(window,key) == 1

    fun isMouseButtonDown(button: Int) = glfwGetMouseButton(window,button) == 1

    fun isKeyPressed(key: Int) = isKeyDown(key) && !keys[key]!!
    fun isKeyReleased(key: Int) = !isKeyDown(key) && keys[key]!!

    fun update() {
        for (i in 32..<GLFW_KEY_LAST) {
            keys[i] = isKeyDown(i)
        }
    }
}