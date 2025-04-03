package d2t.terra.abubaria.io.graphics.shader

import d2t.terra.abubaria.io.graphics.Window
import org.lwjgl.opengl.GL20.glBindAttribLocation
import org.lwjgl.opengl.GL20.glGetUniformLocation
import org.lwjgl.opengl.GL20.glUniform1f
import org.lwjgl.opengl.GL20.glUniform1i
import org.lwjgl.opengl.GL20.glUniform2f
import org.lwjgl.opengl.GL20.glUniform4f
import org.joml.Matrix4f

class BatchShader : Shader("shaders/batch") {
    private var timeLoc: Int = 0
    private var colorLoc: Int = 0
    private var typeLoc: Int = 0
    private var samplerLoc: Int = 0
    private var cameraScaleLoc: Int = 0
    private var cameraCenterLoc: Int = 0
    
    override fun build() {
        loadVertexShader()
        loadFragmentShader()
        
        glBindAttribLocation(program, 0, "rectData")
        glBindAttribLocation(program, 1, "uvData")
        glBindAttribLocation(program, 2, "zIndex")
        glBindAttribLocation(program, 3, "colorData")
        glBindAttribLocation(program, 4, "transformData")
        glBindAttribLocation(program, 5, "ignoreCamera")
        glBindAttribLocation(program, 6, "renderType")
        
        linkAndValidateProgram()
        
        timeLoc = glGetUniformLocation(program, "time")
        colorLoc = glGetUniformLocation(program, "color")
        typeLoc = glGetUniformLocation(program, "type")
        projectionLoc = glGetUniformLocation(program, "projection")
        samplerLoc = glGetUniformLocation(program, "sampler")
        cameraScaleLoc = glGetUniformLocation(program, "cameraScale")
        cameraCenterLoc = glGetUniformLocation(program, "cameraCenter")
        
        bind()
        glUniform1i(samplerLoc, 0)
        setColor(1f, 1f, 1f, 1f)
        setType(0f)
        setCameraScale(1f, 1f)
        setCameraCenter(Window.centerX, Window.centerY)
        setProjection(Matrix4f().setOrtho2D(0f, Window.width.toFloat(), Window.height.toFloat(), 0f))
    }
    
    fun setTime(time: Float) {
        glUniform1f(timeLoc, time)
    }
    
    fun setColor(r: Float, g: Float, b: Float, a: Float) {
        glUniform4f(colorLoc, r, g, b, a)
    }
    
    fun setType(type: Float) {
        glUniform1f(typeLoc, type)
    }
    
    fun setCameraScale(scaleX: Float, scaleY: Float) {
        glUniform2f(cameraScaleLoc, scaleX, scaleY)
    }
    
    fun setCameraCenter(x: Float, y: Float) {
        glUniform2f(cameraCenterLoc, x, y)
    }
} 