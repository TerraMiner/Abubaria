package d2t.terra.abubaria.io.fonts

import d2t.terra.abubaria.io.graphics.Model

class CharInfo(
    val inAtlasX: Int,
    val inAtlasY: Int,
    val inAtlasWidth: Int,
    val inAtlasHeight: Int,
    val xOffset: Int = 0,
    val yOffset: Int = 0,
    val advanceWidth: Int
) {
    lateinit var model: Model

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