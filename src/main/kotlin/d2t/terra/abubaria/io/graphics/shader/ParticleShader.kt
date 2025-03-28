package d2t.terra.abubaria.io.graphics.shader

import org.lwjgl.opengl.GL20.*
import kotlin.properties.Delegates

class ParticleShader(fileName: String) : Shader(fileName) {
    private var samplerLoc by Delegates.notNull<Int>()
    private var timeLoc by Delegates.notNull<Int>()
    private var gridSizeLoc by Delegates.notNull<Int>()
    private var lifeSpanLoc by Delegates.notNull<Int>()

    override fun build() {
        loadVertexShader()
        loadFragmentShader()
        loadGeometryShader()

        glBindAttribLocation(program, 0, "texCoord")

        linkAndValidateProgram()

        samplerLoc = glGetUniformLocation(program, "sampler")
        timeLoc = glGetUniformLocation(program, "time")
        gridSizeLoc = glGetUniformLocation(program, "gridSize")
        lifeSpanLoc = glGetUniformLocation(program, "lifeSpan")
        transform.allocate(program)
        projectionLoc = glGetUniformLocation(program, "projection")

        bind()
        glUniform1i(samplerLoc, 0)
        glUniform1f(timeLoc, 0.0f)
        glUniform2f(gridSizeLoc, 1.0f, 1.0f)
        glUniform1f(lifeSpanLoc, 1.0f)
    }

    fun setTime(time: Float) {
        bind()
        glUniform1f(timeLoc, time)
    }

    fun setGridSize(x: Float, y: Float) {
        bind()
        glUniform2f(gridSizeLoc, x, y)
    }

    fun setLifeSpan(lifeSpan: Float) {
        bind()
        glUniform1f(lifeSpanLoc, lifeSpan)
    }
}
