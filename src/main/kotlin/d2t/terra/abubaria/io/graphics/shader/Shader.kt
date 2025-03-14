package d2t.terra.abubaria.io.graphics.shader

import d2t.terra.abubaria.io.graphics.shader.module.ShaderTransformModule
import d2t.terra.abubaria.util.loopWhile
import org.joml.Matrix4f
import org.lwjgl.opengl.GL20.glUniformMatrix4fv
import org.lwjgl.opengl.GL20.glUseProgram
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.nio.FloatBuffer

abstract class Shader {
    protected var program: Int = 0
    protected var vs: Int = 0
    protected var fs: Int = 0

    var transform = ShaderTransformModule()

    protected var projectionLoc: Int = 0
    protected var isRegistered = false
    protected lateinit var buffer: FloatBuffer

    abstract fun register()

    fun setProjection(value: Matrix4f) {
        buffer.clear()

        loopWhile(0, 4) { col ->
            loopWhile(0, 4) { row ->
                buffer.put(value.get(col, row))
            }
        }

        buffer.flip()
        glUniformMatrix4fv(projectionLoc, false, buffer)
    }

    fun bind() {
        if (activeShaderProgram == program) return
        glUseProgram(program)
        activeShaderProgram = program
    }

    companion object {
        var activeShaderProgram: Int = -1

        @JvmStatic
        protected fun loadShader(path: String): String {
            val resource = javaClass.getClassLoader().getResource(path.replace("res/", ""))!!
            val string = StringBuilder()
            val br = BufferedReader(FileReader(File(resource.toURI())))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                string.append(line)
                string.append("\n")
            }
            br.close()
            return string.toString()
        }
    }
}