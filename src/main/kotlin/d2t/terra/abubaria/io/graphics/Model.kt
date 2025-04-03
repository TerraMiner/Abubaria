package d2t.terra.abubaria.io.graphics

class Model(val uvX: Float, val uvY: Float, val uvMX: Float, val uvMY: Float) {
    companion object {
        val DEFAULT = Model(0f, 0f, 1f, 1f)
    }
}