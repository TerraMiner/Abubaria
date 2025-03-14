package d2t.terra.abubaria.io.graphics.shader

import d2t.terra.abubaria.io.graphics.shader.module.ShaderColorModule
import d2t.terra.abubaria.io.graphics.shader.module.ShaderAnglerModule
import d2t.terra.abubaria.io.graphics.shader.module.ShaderModule
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.*
import kotlin.properties.Delegates
import kotlin.system.exitProcess

class TextureShader(val fileName: String) : Shader() {
    val angler: ShaderAnglerModule = ShaderAnglerModule()
    val colorPalette: ShaderColorModule = ShaderColorModule()
    private var samplerLoc by Delegates.notNull<Int>()

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

        glBindAttribLocation(program, 0, "texCoord")

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

        samplerLoc = glGetUniformLocation(program, "sampler")
        colorPalette.allocate(program)
        angler.allocate(program)
        transform.allocate(program)
        projectionLoc = glGetUniformLocation(program, "projection")

        bind()
        glUniform1i(samplerLoc, 0)
        colorPalette.setColor(1f, 1f, 1f, 1f)
        angler.setAngle(0f)

        isRegistered = true
    }

    fun performSnapshot(module: ShaderModule, action: () -> Unit) {
        bind()
        module.performSnapshot(program,action)
    }
}