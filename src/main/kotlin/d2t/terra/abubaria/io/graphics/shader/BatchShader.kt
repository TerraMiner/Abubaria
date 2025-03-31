package d2t.terra.abubaria.io.graphics.shader

import d2t.terra.abubaria.io.graphics.Window
import org.lwjgl.opengl.GL20.glBindAttribLocation
import org.lwjgl.opengl.GL20.glGetUniformLocation
import org.lwjgl.opengl.GL20.glUniform1f
import org.lwjgl.opengl.GL20.glUniform1i
import org.lwjgl.opengl.GL20.glUniform2f
import org.joml.Matrix4f

class BatchShader : TextureShader("shaders/batch") {
    private var timeLoc: Int = 0
    private var viewLoc: Int = 0
    
    override fun build() {
        loadVertexShader()
        loadFragmentShader()
        
        glBindAttribLocation(program, 0, "vertexData")
        glBindAttribLocation(program, 1, "colorData")
        glBindAttribLocation(program, 2, "instanceData")
        
        linkAndValidateProgram()
        
        timeLoc = glGetUniformLocation(program, "time")
        viewLoc = glGetUniformLocation(program, "view")
        projectionLoc = glGetUniformLocation(program, "projection")
        samplerLoc = glGetUniformLocation(program, "sampler")
        
        colorPalette.allocate(program)
        
        bind()
        glUniform1i(samplerLoc, 0)
        colorPalette.setColor(1f, 1f, 1f, 1f)
        setProjection(Matrix4f().setOrtho2D(0f, Window.width.toFloat(), Window.height.toFloat(), 0f))
    }
    
    fun setTime(time: Float) {
        glUniform1f(timeLoc, time)
    }
    
    fun setView(x: Float, y: Float) {
        glUniform2f(viewLoc, x, y)
    }
} 