package d2t.terra.abubaria.io.graphics.render

import d2t.terra.abubaria.io.graphics.shader.GeometryShader

object RendererManager {
    lateinit var renderers: Array<Renderer<*, *>>
    val WorldRenderer get() = renderers[0] as WorldRenderer
    val UIRenderer get() = renderers[1] as UIRenderer
    val ParticleRenderer get() = renderers[2] as ParticleRenderer
    val GeometryWorldRenderer get() = renderers[3] as GeometryWorldRenderer
//    val GeometryRenderer get() = renderers[3] as GeometryRenderer

    fun setup() {
        renderers = arrayOf(WorldRenderer(), UIRenderer(), ParticleRenderer(), GeometryWorldRenderer())
        renderers.forEach(Renderer<*, *>::register)
    }

    fun updateProjections() {
        renderers.forEach(Renderer<*, *>::updateProjection)
    }
}