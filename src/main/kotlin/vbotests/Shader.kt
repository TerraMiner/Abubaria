package vbotests

import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.ARBGpuShaderFp64.glUniform1
import org.lwjgl.opengl.GL20.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.lang.StringBuilder
import java.nio.FloatBuffer
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
    }

    fun setUniform(name: String, value: Int) {
        val location = glGetUniformLocation(program, name)
        if (location != -1) {
            glUniform1i(location, value)
        }
    }

    fun setUniform(name: String, value: Matrix4f) {
        val location = glGetUniformLocation(program, name)
        val buffer = BufferUtils.createFloatBuffer(16)
        for (col in 0..3) {
            for (row in 0..3) {
                buffer.put(value.get(col, row))
            }
        }
        buffer.flip()
        if (location != -1) {
            glUniformMatrix4fv(location,false,buffer)
        }
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