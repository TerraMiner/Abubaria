package d2t.terra.abubaria.io.graphics.render

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL14.GL_FUNC_ADD
import org.lwjgl.opengl.GL14.GL_FUNC_REVERSE_SUBTRACT
import org.lwjgl.opengl.GL14.glBlendEquation
import java.nio.FloatBuffer

enum class Layer(val value: Float) {
    WORLD_BLOCKS_LAYER(0F),
    WORLD_ENTITY_LAYER(.1F),
    WORLD_PLAYER_LAYER(.15F),
    WORLD_LIGHT_LAYER(.2f),
    WORLD_DEBUG_LAYER(.25F),
    UI_HUD_ELEMENTS_LAYER(.3F),
    UI_HUD_CONTENTS_LAYER(.4F),
    UI_HUD_TEXT_LAYER(.5F),
    UI_DEBUG_LAYER(.9F),
    UI_CURSOR_LAYER(.99F),
    UI_CURSOR_TEXT_LAYER(1F);

    val textures = Int2ObjectArrayMap<FloatBuffer>()
}