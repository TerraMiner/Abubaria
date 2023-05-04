package d2t.terra.abubaria

import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.player.Camera

object Client {
    var debugMode = false

    var zoom = 0
    var currentZoom = 0

    val maxZoom = 200

    fun zoomIn() {
        zoom = 1
        applyZoom()
        zoom = 0
    }

    fun zoomOut() {
        zoom = -1
        applyZoom()
        zoom = 0
    }

    fun applyZoom() {

        if (currentZoom + zoom < 0) {
            currentZoom = 0
            return
        }

        if (currentZoom + zoom > maxZoom) {
            currentZoom = maxZoom
            return
        }

        currentZoom += zoom

        Camera.box.apply {
            width -= zoom*2
            height -= zoom
            x += zoom
            y += zoom
        }
    }

}