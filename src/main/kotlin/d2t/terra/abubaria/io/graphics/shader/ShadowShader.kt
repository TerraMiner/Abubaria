package d2t.terra.abubaria.io.graphics.shader

import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.*
import java.io.File

class ShadowShader(val fileName: String) : Shader() {
    private var alphaLoc = 0

    override fun register() {
        if (isRegistered) return

        program = glCreateProgram()
        vs = glCreateShader(GL_VERTEX_SHADER)
        fs = glCreateShader(GL_FRAGMENT_SHADER)

        glShaderSource(vs, loadShader("$fileName.vert"))
        glCompileShader(vs)
        checkCompileStatus(vs)

        glShaderSource(fs, loadShader("$fileName.frag"))
        glCompileShader(fs)
        checkCompileStatus(fs)

        glAttachShader(program, vs)
        glAttachShader(program, fs)

        glBindAttribLocation(program, 0, "vertices")

        glLinkProgram(program)
        checkLinkStatus()

        alphaLoc = glGetUniformLocation(program, "alpha")
        projectionLoc = glGetUniformLocation(program, "projection")

        buffer = BufferUtils.createFloatBuffer(16)

        isRegistered = true
    }

    fun setAlpha(value: Float) {
        glUniform1f(alphaLoc, value)
    }

    private fun checkCompileStatus(shader: Int) {
        if (glGetShaderi(shader, GL_COMPILE_STATUS) != GL_TRUE) {
            throw RuntimeException(glGetShaderInfoLog(shader))
        }
    }

    private fun checkLinkStatus() {
        if (glGetProgrami(program, GL_LINK_STATUS) != GL_TRUE) {
            throw RuntimeException(glGetProgramInfoLog(program))
        }
    }

    companion object {
        private fun loadShader(path: String): String {
            return File(path).readText()
        }
    }
}
