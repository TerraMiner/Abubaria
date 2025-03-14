package d2t.terra.abubaria.io.graphics.render

object RendererManager {
    lateinit var renderers: Array<Renderer<*, *>>
    val WorldRenderer get() = renderers[0] as WorldRenderer
    val UIRenderer get() = renderers[1] as UIRenderer
//    val LightRenderer get() = renderers[2]

    fun setup() {
        renderers = arrayOf(WorldRenderer(), UIRenderer()/*, ShadowRenderer()*/)
        renderers.forEach(Renderer<*, *>::register)
    }

    fun updateProjections() {
        renderers.forEach(Renderer<*, *>::updateProjection)
    }
}