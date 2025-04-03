package d2t.terra.abubaria.io.graphics.shader

import d2t.terra.abubaria.io.graphics.shader.environment.ShaderEnvironment
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.*
import java.nio.FloatBuffer
import kotlin.system.exitProcess

abstract class Shader(val fileName: String) {
    protected var program: Int = 0

    protected val vertexShader = ShaderEnvironment(GL_VERTEX_SHADER, "$fileName.vert")
    protected val fragmentShader = ShaderEnvironment(GL_FRAGMENT_SHADER, "$fileName.frag")

    protected var projectionLoc: Int = 0
    private var isRegistered = false
    protected val projectionBuffer: FloatBuffer = BufferUtils.createFloatBuffer(16)

    protected abstract fun build()

    fun register() {
        if (isRegistered) {
            println("Shader $fileName already registered!")
            return
        }
        program = glCreateProgram()
        build()
        isRegistered = true
    }

    fun loadVertexShader() {
        vertexShader.loadAndAttach(program)
    }

    fun loadFragmentShader() {
        fragmentShader.loadAndAttach(program)
    }

    fun linkAndValidateProgram() {
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
    }

    fun setProjection(value: Matrix4f) {
        projectionBuffer.clear()
        value.get(projectionBuffer)
        glUniformMatrix4fv(projectionLoc, false, projectionBuffer)
    }

    fun bind() {
        if (activeShaderProgram == program) return
        glUseProgram(program)
        activeShaderProgram = program
    }

    companion object {
        var activeShaderProgram: Int = -1
    }
}