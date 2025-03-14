package d2t.terra.abubaria.world.material

enum class MaterialState(val offset: Float, val scale: Float) {
    BOTTOM(.5F, .5F), UPPER(0F, .5F), FULL(0F, 0F)
}