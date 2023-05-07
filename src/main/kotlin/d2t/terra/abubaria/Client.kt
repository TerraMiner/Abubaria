package d2t.terra.abubaria

object Client {
    var debugMode = false

    var zoom = .001f

    val minZoom = 0.75f
    val maxZoom = 2f
    var currentZoom = 1f

    fun zoomIn() {
        if (currentZoom + zoom > maxZoom) {
            currentZoom = maxZoom
        } else currentZoom += zoom
    }

    fun zoomOut() {
        if (currentZoom - zoom < minZoom) {
            currentZoom = minZoom
        } else currentZoom -= zoom
    }
}