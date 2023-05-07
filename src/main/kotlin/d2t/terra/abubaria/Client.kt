package d2t.terra.abubaria

import d2t.terra.abubaria.GamePanel.tileSize
import d2t.terra.abubaria.entity.player.Camera
import org.lwjgl.opengl.GL

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

//    fun applyZoom() {
//        Camera.box.apply {
//            println("current: $currentZoom")
//            val zoomFactor = 23f
//            val zoomLvl = String.format("%.2f",
//                kotlin.math.round(currentZoom * zoomFactor) / zoomFactor
//                ).replace(",",".").toDouble()
//
//            println("rounded: $zoomLvl")
//            width = (GamePanel.screenWidth / zoomLvl).toDouble()
//            height = (GamePanel.screenHeight / zoomLvl).toDouble()
////            x = width
////            y = height
//        }
//    }

}