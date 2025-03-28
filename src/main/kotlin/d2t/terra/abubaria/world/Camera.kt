package d2t.terra.abubaria.world

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.chunkSize
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.tileSizeF
import d2t.terra.abubaria.util.print

object Camera {
    var cameraX = 0f
    var cameraY = 0f

    fun coerceInWorld(location: Location) {
        val leftCorner = GamePanel.world.worldBorder.x
        val rightCorner = Window.width - GamePanel.world.worldBorder.maxX
        cameraX = Window.centerX.toFloat() - location.x.toFloat()
        if (cameraX > leftCorner) cameraX = leftCorner
        if (cameraX < rightCorner) cameraX = rightCorner

        val upCorner = GamePanel.world.worldBorder.y
        val bottomCorner = Window.height - GamePanel.world.worldBorder.maxY
        cameraY = Window.centerY.toFloat() - location.y.toFloat()
        if (cameraY > upCorner) cameraY = upCorner
        if (cameraY < bottomCorner) cameraY = bottomCorner
    }

    fun playerScreenPosX(targetLocation: Location): Float {
        var offX = if (cameraX == GamePanel.world.worldBorder.x) targetLocation.x
        else if (cameraX == Window.width - GamePanel.world.worldBorder.maxX) cameraX + targetLocation.x
        else Window.centerX
        return offX.toFloat()
    }

    fun playerScreenPosY(targetLocation: Location): Float {
        var offX = if (cameraY == GamePanel.world.worldBorder.y) targetLocation.y
        else if (cameraY == Window.height - GamePanel.world.worldBorder.maxY) cameraY + targetLocation.y
        else Window.centerY
        return offX.toFloat()
    }
}