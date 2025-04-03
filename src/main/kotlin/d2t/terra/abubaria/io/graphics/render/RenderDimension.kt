package d2t.terra.abubaria.io.graphics.render

import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.Camera
import org.joml.Vector2f

enum class RenderDimension(val offset: () -> Location) {
    WORLD({ Camera.renderLocation }), SCREEN({ Camera.noop })
}