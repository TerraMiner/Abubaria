package vbotests.render

import d2t.terra.abubaria.io.LagDebugger
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.*
import vbotests.game.cleaner
import vbotests.util.loopWhile
import java.lang.ref.Cleaner
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.lang.StringBuilder
import kotlin.system.exitProcess

class Shader(fileName: String) {
    private var program: Int = glCreateProgram()
    private var vs: Int = glCreateShader(GL_VERTEX_SHADER)
    private var fs: Int = glCreateShader(GL_FRAGMENT_SHADER)

    init {
        glShaderSource(vs,readFile("$fileName.vs"))
        glCompileShader(vs)
        if (glGetShaderi(vs, GL_COMPILE_STATUS) != 1){
            System.err.println(glGetShaderInfoLog(vs))
            exitProcess(1)
        }

        glShaderSource(fs,readFile("$fileName.fs"))
        glCompileShader(fs)
        if (glGetShaderi(fs, GL_COMPILE_STATUS) != 1){
            System.err.println(glGetShaderInfoLog(fs))
            exitProcess(1)
        }

        glAttachShader(program,vs)
        glAttachShader(program,fs)

        glBindAttribLocation(program,0,"vertices")
        glBindAttribLocation(program,1,"textures")

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

        cleaner.register(this) {
            glDetachShader(program,vs)
            glDetachShader(program,fs)
            glDeleteShader(vs)
            glDeleteShader(fs)
            glDeleteProgram(program)
        }
    }

    val samplerLoc = glGetUniformLocation(program, "sampler")
    val projectionLoc = glGetUniformLocation(program, "projection")

    fun setSamplerUniform(value: Int) {
        glUniform1i(samplerLoc, value)
    }

    val buffer = BufferUtils.createFloatBuffer(16)

    fun setProjectionUniform(value: Matrix4f) {
        buffer.clear()

        loopWhile(0,3) { col ->
            loopWhile(0,3) { row ->
                buffer.put(value.get(col, row))
            }
        }

        buffer.flip()
        glUniformMatrix4fv(projectionLoc,false,buffer)
    }

    fun bind() {
        glUseProgram(program)
    }

    private fun readFile(fileName: String): String {
        val string = StringBuilder()
        val br = BufferedReader(FileReader(File("res/shaders/$fileName")))
        var line: String?
        while (br.readLine().also{ line = it } != null) {
            string.append(line)
            string.append("\n")
        }
        br.close()
        return string.toString()
    }
}