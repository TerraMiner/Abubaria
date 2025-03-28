package d2t.terra.abubaria.io.fonts

import d2t.terra.abubaria.io.graphics.render.TextureRenderer
import d2t.terra.abubaria.io.graphics.Model

class CharInfo(
    val inAtlasX: Int,     // X-координата в атласе
    val inAtlasY: Int,     // Y-координата в атласе
    val inAtlasWidth: Int,       // Ширина символа
    val inAtlasHeight: Int,      // Высота символа
    val xOffset: Int = 0, // Горизонтальное смещение для рендеринга
    val yOffset: Int = 0, // Вертикальное смещение для рендеринга (относительно базовой линии)
    val advanceWidth: Int // Расстояние для перемещения курсора после рендеринга этого символа
) {
    lateinit var model: Model

    fun render(renderer: TextureRenderer, font: CFont, x: Float, y: Float, scale: Float = 1f) {
        renderer.render(
            font.imageFont,
            model,
            x + (xOffset * scale),
            y - (yOffset * scale),
            inAtlasWidth * scale,
            inAtlasHeight * scale
        )
    }

    fun buildModel(atlasWidth: Int, atlasHeight: Int) {
        model = Model(let {
            val normalizedTexX = inAtlasX.toFloat() / atlasWidth
            val normalizedTexY = inAtlasY.toFloat() / atlasHeight
            val normalizedTexWidth = inAtlasWidth.toFloat() / atlasWidth
            val normalizedTexHeight = inAtlasHeight.toFloat() / atlasHeight
            floatArrayOf(
                normalizedTexX, normalizedTexY + normalizedTexHeight,
                normalizedTexX + normalizedTexWidth, normalizedTexY + normalizedTexHeight,
                normalizedTexX + normalizedTexWidth, normalizedTexY,
                normalizedTexX, normalizedTexY
            )
        })
    }
}