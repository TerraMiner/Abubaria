import d2t.terra.abubaria.io.graphics.Model
import d2t.terra.abubaria.io.graphics.Texture
import d2t.terra.abubaria.io.graphics.render.BatchRenderer

//class ParticleBatchRenderer : BatchRenderer() {
//    private var currentTime = 0f
//
//    fun drawParticle(texture: Texture, x: Float, y: Float, width: Float, height: Float) {
//        batchBuffer.getOrPut(texture) { ArrayList() }.add(
//            BatchElement(x, y, width, height, Model.DEFAULT, 0f, 1f, 1f, 1f, 1f, RenderType.PARTICLE)
//        )
//    }
//
//    override fun flush() {
//        shader.bind()
//        shader.setTime(currentTime)
//        super.flush()
//    }
//
//    fun update(deltaTime: Float) {
//        currentTime += deltaTime
//    }
//}