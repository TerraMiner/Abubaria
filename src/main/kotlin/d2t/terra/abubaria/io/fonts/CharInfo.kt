package d2t.terra.abubaria.io.fonts

import d2t.terra.abubaria.io.graphics.render.TextureRenderer
import d2t.terra.abubaria.io.graphics.Model

class CharInfo(val sourceX: Int, val sourceY: Int, val width: Int, val height: Int) {
    lateinit var model: Model
    fun render(renderer: TextureRenderer, font: CFont, x: Float, y: Float, scale: Float = 1f) {
        renderer.render(font.imageFont, model, x, y, width * scale, height * scale)
    }
    fun buildModel(atlasWidth: Int, atlasHeight: Int) {
        model = Model(let {
            val normalizedTexX = sourceX.toFloat() / atlasWidth
            val normalizedTexY = sourceY.toFloat() / atlasHeight
            val normalizedTexWidth = width.toFloat() / atlasWidth
            val normalizedTexHeight = height.toFloat() / atlasHeight
            floatArrayOf(
                normalizedTexX, normalizedTexY + normalizedTexHeight,
                normalizedTexX + normalizedTexWidth, normalizedTexY + normalizedTexHeight,
                normalizedTexX + normalizedTexWidth, normalizedTexY,
                normalizedTexX, normalizedTexY
            )
        })
    }
}