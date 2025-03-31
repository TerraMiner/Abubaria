package d2t.terra.abubaria.io.graphics

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class Model(val texCoords: FloatArray) {

    companion object {
        val DEFAULT = Model(floatArrayOf(
            0f, 1f,
            1f, 1f,
            1f, 0f,
            0f, 0f
        ))
    }
}