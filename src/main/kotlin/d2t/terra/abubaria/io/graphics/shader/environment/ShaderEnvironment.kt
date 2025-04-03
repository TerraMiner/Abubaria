package d2t.terra.abubaria.io.graphics.shader.environment

import org.lwjgl.opengl.GL20.GL_COMPILE_STATUS
import org.lwjgl.opengl.GL20.glAttachShader
import org.lwjgl.opengl.GL20.glCompileShader
import org.lwjgl.opengl.GL20.glCreateShader
import org.lwjgl.opengl.GL20.glGetShaderInfoLog
import org.lwjgl.opengl.GL20.glGetShaderi
import org.lwjgl.opengl.GL20.glShaderSource
import kotlin.system.exitProcess

class ShaderEnvironment(val type: Int, val fileName: String) {
    var id: Int = 0
    
    fun loadAndAttach(program: Int) {
        id = glCreateShader(type)
        glShaderSource(id, load(fileName))
        glCompileShader(id)
        if (glGetShaderi(id, GL_COMPILE_STATUS) != 1) {
            System.err.println(glGetShaderInfoLog(id))
            exitProcess(1)
        }
        glAttachShader(program, id)
    }

    private fun load(path: String): String {
        return javaClass.getClassLoader().getResource(path.replace("res/", ""))!!.readText()
    }
}