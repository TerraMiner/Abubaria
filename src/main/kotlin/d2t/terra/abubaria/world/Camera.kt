package d2t.terra.abubaria.world

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.blockShiftBits
import d2t.terra.abubaria.chunkShiftBits
import d2t.terra.abubaria.entity.impl.ClientPlayer
import d2t.terra.abubaria.io.graphics.Window
import d2t.terra.abubaria.io.graphics.render.Renderer
import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.util.Cooldown
import kotlin.math.abs
import kotlin.math.floor

object Camera {
    val cameraTargetLocation: Location get() = ClientPlayer.centerLocation

    val noop = Location(0f, 0f)
    val view: Location = Location(0f,0f)
    var renderView: Location = view.clone

    const val MIN_ZOOM_VAL = 1f
    const val MAX_ZOOM_VAL = 2f
    const val ZOOM_SPEED = .1f
    
    var zoomValue: Float = 1f
    var lastAppliedZoomValue = zoomValue
    var zoomCooldown = Cooldown(50).apply(Cooldown::start)

    private var targetZoomValue = 1.0f
    private var previousZoomValue = 1.0f
    private var currentZoomValue = 1.0f

    fun zoomIn() {
        if (!zoomCooldown.isEnded()) return
        if (targetZoomValue + ZOOM_SPEED > MAX_ZOOM_VAL) targetZoomValue = MAX_ZOOM_VAL
        else targetZoomValue += ZOOM_SPEED
        zoomCooldown.start()
    }

    fun zoomOut() {
        if (!zoomCooldown.isEnded()) return
        if (targetZoomValue - ZOOM_SPEED < MIN_ZOOM_VAL) targetZoomValue = MIN_ZOOM_VAL
        else targetZoomValue -= ZOOM_SPEED
        zoomCooldown.start()
    }

    fun initialize() {
        coerceInWorld()
        currentZoomValue = zoomValue
        previousZoomValue = zoomValue
    }

    fun getWorldPosX(screenX: Float): Float {
        val screenCenter = Window.width / 2f
        val offsetFromCenter = (screenCenter - screenX) / zoomValue - screenCenter
        return -view.x - offsetFromCenter
    }

    fun getWorldPosY(screenY: Float): Float {
        val screenCenter = Window.height / 2f
        val offsetFromCenter = (screenCenter - screenY) / zoomValue - screenCenter
        return -view.y - offsetFromCenter
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

        renderView = view.clone
    }

    fun tick() {
        coerceInWorld()
        previousZoomValue = currentZoomValue
        if (currentZoomValue != targetZoomValue) {
            val zoomDiff = targetZoomValue - currentZoomValue
            
            currentZoomValue += zoomDiff * 0.1f
            
            if (abs(currentZoomValue - targetZoomValue) < 0.01f) {
                currentZoomValue = targetZoomValue
            }
        }
        zoomValue = currentZoomValue
        
        // Округляем координаты камеры до целых пикселей
        view.x = floor(view.x)
        view.y = floor(view.y)
    }

    fun applyZoom() {
        Renderer.setCameraScale(zoomValue)
        lastAppliedZoomValue = zoomValue
    }
}