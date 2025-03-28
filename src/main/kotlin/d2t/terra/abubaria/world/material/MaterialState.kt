package d2t.terra.abubaria.world.material

enum class MaterialState(val offset: Float, val scale: Float) {
    BOTTOM(.5f, .5f), UPPER(0f, .5f), FULL(0f, 0f)
}