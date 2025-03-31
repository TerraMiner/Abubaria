package d2t.terra.abubaria.io.graphics.shader

import d2t.terra.abubaria.io.graphics.shader.environment.ShaderEnvironment
import d2t.terra.abubaria.io.graphics.shader.module.ShaderModule
import d2t.terra.abubaria.io.graphics.shader.module.ShaderTransformModule
import d2t.terra.abubaria.util.loopWhile
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER
import java.nio.FloatBuffer
import kotlin.system.exitProcess

abstract class Shader(val fileName: String) {
    protected var program: Int = 0

    protected val vertexShader = ShaderEnvironment(GL_VERTEX_SHADER, "$fileName.vert")
    protected val fragmentShader = ShaderEnvironment(GL_FRAGMENT_SHADER, "$fileName.frag")
    protected val geometryShader = ShaderEnvironment(GL_GEOMETRY_SHADER, "$fileName.geom")

    protected var projectionLoc: Int = 0
    private var isRegistered = false
    protected lateinit var projectionBuffer: FloatBuffer

    protected abstract fun build()

    fun register() {
        if (isRegistered) {
            println("Shader $fileName already registered!")
            return
        }
        program = glCreateProgram()
        projectionBuffer = BufferUtils.createFloatBuffer(16)
        build()
        isRegistered = true
    }

    fun loadVertexShader() {
        vertexShader.loadAndAttach(program)
    }

    fun loadFragmentShader() {
        fragmentShader.loadAndAttach(program)
    }

    fun loadGeometryShader() {
        geometryShader.loadAndAttach(program)
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

        loopWhile(0, 4) { col ->
            loopWhile(0, 4) { row ->
                projectionBuffer.put(value.get(col, row))
            }
        }

        projectionBuffer.flip()
        glUniformMatrix4fv(projectionLoc, false, projectionBuffer)
    }

    open fun performSnapshot(module: ShaderModule<*,*>, action: () -> Unit) {
        bind()
        module.performSnapshot(action)
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