package d2t.terra.abubaria.io.graphics.render

import d2t.terra.abubaria.location.Location
import d2t.terra.abubaria.world.Camera

enum class RenderDimension(val offset: () -> Location) {
    WORLD({ Camera.renderView }), SCREEN({ Camera.noop })
}