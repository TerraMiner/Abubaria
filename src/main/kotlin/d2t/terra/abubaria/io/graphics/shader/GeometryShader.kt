package d2t.terra.abubaria.io.graphics.shader

import d2t.terra.abubaria.io.graphics.shader.module.ShaderColorModule
import d2t.terra.abubaria.io.graphics.shader.module.geometry.ShaderShapeModule
import org.lwjgl.opengl.GL20.glGetUniformLocation

class GeometryShader(fileName: String) : Shader(fileName) {
    val shape: ShaderShapeModule = ShaderShapeModule()
    val colorPalette: ShaderColorModule = ShaderColorModule()

    override fun build() {
        loadVertexShader()
        loadFragmentShader()

        linkAndValidateProgram()
        shape.allocate(program)
        colorPalette.allocate(program)
        transform.allocate(program)
        projectionLoc = glGetUniformLocation(program, "projection")

        bind()
        colorPalette.setColor(1f, 1f, 1f, 1f)
    }
}