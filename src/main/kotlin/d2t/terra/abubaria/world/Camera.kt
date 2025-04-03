package d2t.terra.abubaria.world

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.blockShiftBits
import d2t.terra.abubaria.chunkShiftBits
import d2t.terra.abubaria.entity.MovingObject
import d2t.terra.abubaria.entity.impl.ClientPlayer
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.io.graphics.render.Renderer
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.tileSizeF
import d2t.terra.abubaria.util.Cooldown
import kotlin.math.abs
import kotlin.math.floor

object Camera : MovingObject {
    val cameraTargetLocation: Location get() = ClientPlayer.centerLocation

    val noop = Location(0f, 0f)
    val view: Location = Location(0f,0f)

    override val renderLocation = view.clone

    val minZoomValue = 1f
    val maxZoomValue = 2f
    val zoomSpeed = .1f
    var zoomValue: Float = 1f
    var lastAppliedZoomValue = zoomValue
    var zoomCooldown = Cooldown(50).apply(Cooldown::start)

    private var targetZoomValue = 1.0f

    fun zoomIn() {
        if (!zoomCooldown.isEnded()) return
        if (targetZoomValue + zoomSpeed > maxZoomValue) targetZoomValue = maxZoomValue
        else targetZoomValue += zoomSpeed
        zoomCooldown.start()
    }

    fun zoomOut() {
        if (!zoomCooldown.isEnded()) return
        if (targetZoomValue - zoomSpeed < minZoomValue) targetZoomValue = minZoomValue
        else targetZoomValue -= zoomSpeed
        zoomCooldown.start()
    }

    fun applyZoom() {
        if (zoomValue != targetZoomValue) {
            zoomValue = lerp(zoomValue, targetZoomValue, interpSpeed(0.025f))

            if (abs(zoomValue - targetZoomValue) < 0.0001f) {
                zoomValue = targetZoomValue
            }

            Renderer.setCameraScale(zoomValue)
            lastAppliedZoomValue = zoomValue
        }
    }

    fun initialize() {
        coerceInWorld()
        renderLocation.set(view)
    }

    fun getWorldPosX(screenX: Float): Float {
        val screenCenter = Window.width / 2f
        val offsetFromCenter = (screenCenter - screenX) / zoomValue - screenCenter
        return -renderLocation.x - offsetFromCenter
    }

    fun getWorldPosY(screenY: Float): Float {
        val screenCenter = Window.height / 2f
        val offsetFromCenter = (screenCenter - screenY) / zoomValue - screenCenter
        return -renderLocation.y - offsetFromCenter
    }

    fun getWorldBlockX(screenX: Float) = floor(getWorldPosX(screenX)).toInt() shr blockShiftBits
    fun getWorldBlockY(screenY: Float) = floor(getWorldPosY(screenY)).toInt() shr blockShiftBits

    fun getWorldChunkX(screenX: Float) = getWorldBlockX(screenX) shr chunkShiftBits
    fun getWorldChunkY(screenY: Float) = getWorldBlockY(screenY) shr chunkShiftBits

    fun coerceInWorld() {
        view.x = Window.centerX - cameraTargetLocation.x
        view.y = Window.centerY - cameraTargetLocation.y

        val halfVisibleWidth = (Window.width / (2 * zoomValue))
        val halfVisibleHeight = (Window.height / (2 * zoomValue))

        val minViewX = Window.centerX - halfVisibleWidth
        val maxViewX = Window.centerX - (GamePanel.world.border.maxX - halfVisibleWidth)

        val minViewY = Window.centerY - halfVisibleHeight
        val maxViewY = Window.centerY - (GamePanel.world.border.maxY - halfVisibleHeight)

        if (view.x > minViewX) view.x = minViewX
        if (view.x < maxViewX) view.x = maxViewX

        if (view.y > minViewY) view.y = minViewY
        if (view.y < maxViewY) view.y = maxViewY
    }

    fun interpolate() {
        updateRenderLocation(view, .05f)
    }

}