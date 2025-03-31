package d2t.terra.abubaria.io.graphics.render

object RendererManager {
    lateinit var renderers: Array<BatchRenderer>
    val WorldRenderer get() = renderers[0] as WorldBatchRenderer
    val UIRenderer get() = renderers[1] as UIBatchRenderer
//    val ParticleRenderer get() = renderers[2] as ParticleBatchRenderer
//    val GeometryRenderer get() = renderers[3] as GeometryBatchRenderer

    fun setup() {
        renderers = arrayOf(
            WorldBatchRenderer(),
            UIBatchRenderer(),
//            ParticleBatchRenderer(),
//            GeometryBatchRenderer()
        )
        renderers.forEach(BatchRenderer::register)
    }

    fun updateProjections() {
        renderers.forEach(BatchRenderer::updateProjection)
    }
}