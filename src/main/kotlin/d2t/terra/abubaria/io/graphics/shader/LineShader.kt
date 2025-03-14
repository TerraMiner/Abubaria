package d2t.terra.abubaria.io.graphics.shader

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.*
import kotlin.properties.Delegates
import kotlin.system.exitProcess

class LineShader(val fileName: String) : Shader() {
    private var colorLoc by Delegates.notNull<Int>()
    private var lineWidthLoc by Delegates.notNull<Int>()

    override fun register() {
        if (isRegistered) {
            println("Shader $fileName already registered!")
            return
        }

        program = glCreateProgram()
        vs = glCreateShader(GL_VERTEX_SHADER)
        fs = glCreateShader(GL_FRAGMENT_SHADER)
        buffer = BufferUtils.createFloatBuffer(16)

        glShaderSource(vs, loadShader("$fileName.vert"))
        glCompileShader(vs)
        if (glGetShaderi(vs, GL_COMPILE_STATUS) != 1) {
            System.err.println(glGetShaderInfoLog(vs))
            exitProcess(1)
        }

        glShaderSource(fs, loadShader("$fileName.frag"))
        glCompileShader(fs)
        if (glGetShaderi(fs, GL_COMPILE_STATUS) != 1) {
            System.err.println(glGetShaderInfoLog(fs))
            exitProcess(1)
        }

        glAttachShader(program, vs)
        glAttachShader(program, fs)

        glBindAttribLocation(program, 0, "vertices")
        glBindAttribLocation(program, 1, "lineWidth")

        glLinkProgram(program)
        if (glGetProgrami(program, GL_LINK_STATUS) != 1) {
            System.err.println(glGetProgramInfoLog(program))
            exitProcess(1)
        }
        glValidateProgram(program)
        if (glGetProgrami(program, GL_VALIDATE_STATUS) != 1) {
            System.err.println(glGetProgramInfoLog(program))
            exitProcess(1)
        }

        colorLoc = glGetUniformLocation(program, "color")
        lineWidthLoc = glGetUniformLocation(program, "lineWidth")
        projectionLoc = glGetUniformLocation(program, "projection")

        bind()

        setColorUniform(1f, 1f, 1f, 1f)
        setLineWidthUniform(1f)

        isRegistered = true
    }

    fun setColorUniform(r: Float, g: Float, b: Float, a: Float) {
        glUniform4f(colorLoc, r, g, b, a)
    }

    fun setLineWidthUniform(width: Float) {
        glUniform1f(lineWidthLoc, width)
    }

}