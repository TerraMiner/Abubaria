package d2t.terra.abubaria.io.graphics

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.location.Direction
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import org.lwjgl.opengl.GL12.GL_NEAREST
import java.awt.Color


fun loadImage(texturePath: String) = Image(null, texturePath)

fun safetyRects(action: () -> Unit) {
    glPushAttrib(GL_CURRENT_BIT or GL_ENABLE_BIT or GL_TRANSFORM_BIT)
    glDisable(GL_TEXTURE_2D)
    glBegin(GL_QUADS)
    action.invoke()
    glEnd()
    glPopAttrib()
}

//fun drawRect(x: Int, y: Int, width: Int, height: Int, color: Color = Color.BLACK) {
//    glColor4i(color.red, color.green, color.blue, color.alpha)
//    glBegin(GL_LINE_LOOP)
//    glVertex2i(x, y)
//    glVertex2i(x + width, y)
//    glVertex2i(x + width, y + height)
//    glVertex2i(x, y + height)
//    glEnd()
//}

fun drawRotatedTexture(textureId: Int?, x: Double, y: Double, width: Double, height: Double, angle: Float, direction: Direction) {
    if (textureId === null) return
    glBindTexture(GL_TEXTURE_2D, textureId)
    glPushMatrix()
    glTranslated(x + width / 2, y + height / 2, .0)
    when (direction) {
        Direction.RIGHT -> {
            glRotatef(angle, 0f, 0f, 1f)
            glTranslated(-width/2, -height / 2, .0)
        }
        Direction.LEFT -> {
            glRotatef(-angle, 0f, 0f, 1f)
            glTranslated(-width/2, -height / 2, .0)
        }
    }
    drawQuadWithTexCoords(0, 0, width.toInt(), height.toInt())
    glPopMatrix()
}

fun drawFillRect(x: Int, y: Int, width: Int, height: Int, alpha: Int) {
    glColor4f(0f, 0f, 0f, alpha / 255f)
    glVertex2i(x, y)
    glVertex2i(x + width, y)
    glVertex2i(x + width, y + height)
    glVertex2i(x, y + height)
}

fun drawString(string: String, x: Int, y: Int, sizeMod: Int, color: Color = Color.WHITE) {
    if (string.isEmpty()) return
    glPushAttrib(GL_CURRENT_BIT or GL_ENABLE_BIT or GL_TRANSFORM_BIT)
    GamePanel.font.apply {
        var xMod = x
        string.forEach {
            val char = getCharacter(it)

            setupTextureParameters()

            glBindTexture(GL_TEXTURE_2D, char.textureId)

            val widthAdjusted = char.width / sizeMod
            val heightAdjusted = char.height / sizeMod
            glColor4f(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)
            glBegin(GL_QUADS)

            glTexCoord2f(0.0f, 0.0f)
            glVertex2i(xMod, y - fontMetrics.descent)

            glTexCoord2f(0.0f, 1.0f)
            glVertex2i(xMod, y + heightAdjusted - fontMetrics.descent)

            glTexCoord2f(1.0f, 1.0f)
            glVertex2i(xMod + widthAdjusted, y + heightAdjusted - fontMetrics.descent)

            glTexCoord2f(1.0f, 0.0f)
            glVertex2i(xMod + widthAdjusted, y - fontMetrics.descent)

            glEnd()

            xMod += widthAdjusted
        }
    }
    glPopAttrib()
}

fun drawTexture(textureId: Int?, x: Int, y: Int, width: Int, height: Int, color: Color = Color.WHITE) {

    if (textureId === null) return

    setupTextureParameters()

    glBindTexture(GL_TEXTURE_2D, textureId)
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    glColor4f(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)

    drawQuadWithTexCoords(x, y, width, height)
}

fun drawQuadWithTexCoords(x: Int, y: Int, width: Int, height: Int) {
    glBegin(GL_QUADS)
    glTexCoord2f(0.0f, 1.0f)
    glVertex2i(x, y + height)
    glTexCoord2f(1.0f, 1.0f)
    glVertex2i(x + width, y + height)
    glTexCoord2f(1.0f, 0.0f)
    glVertex2i(x + width, y)
    glTexCoord2f(0.0f, 0.0f)
    glVertex2i(x, y)
    glEnd()
}

fun setupTextureParameters() {
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
}