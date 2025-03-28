package d2t.terra.abubaria.io.graphics.shader

import d2t.terra.abubaria.io.graphics.shader.module.ShaderColorModule
import d2t.terra.abubaria.io.graphics.shader.module.ShaderAnglerModule
import org.lwjgl.opengl.GL20.*
import kotlin.properties.Delegates

class TextureShader(fileName: String) : Shader(fileName) {
    val angler: ShaderAnglerModule = ShaderAnglerModule()
    val colorPalette: ShaderColorModule = ShaderColorModule()
    private var samplerLoc by Delegates.notNull<Int>()

    override fun build() {
        loadVertexShader()
        loadFragmentShader()

        glBindAttribLocation(program, 0, "texCoord")

        linkAndValidateProgram()
        samplerLoc = glGetUniformLocation(program, "sampler")
        colorPalette.allocate(program)
        angler.allocate(program)
        transform.allocate(program)
        projectionLoc = glGetUniformLocation(program, "projection")

        bind()
        glUniform1i(samplerLoc, 0)
        colorPalette.setColor(1f, 1f, 1f, 1f)
        angler.setAngle(0f)
    }
}