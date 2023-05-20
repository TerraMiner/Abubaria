package d2t.terra.abubaria.io.graphics

import d2t.terra.abubaria.GamePanel
import d2t.terra.abubaria.location.Direction
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import org.lwjgl.opengl.GL12.GL_NEAREST
import org.lwjgl.opengl.GL15.*
import java.awt.Color


fun loadImage(texturePath: String) = Image(null, texturePath)

fun safetyDraw(mode: Int, action: () -> Unit) {
    glPushAttrib(GL_CURRENT_BIT or GL_ENABLE_BIT or GL_TRANSFORM_BIT)
    glDisable(GL_TEXTURE_2D)
    glBegin(mode)
    action.invoke()
    glEnd()
    glPopAttrib()
}

fun drawRect(x: Float, y: Float, width: Float, height: Float, color: Color = Color.BLACK) {
    glColor3i(color.red, color.green, color.blue)
    glVertex2f(x, y)
    glVertex2f(x + width, y)
    glVertex2f(x + width, y + height)
    glVertex2f(x, y + height)
}

fun drawFillRect(x: Float, y: Float, width: Float, height: Float, alpha: Int) {
    glColor4f(0f, 0f, 0f, alpha / 255f)
    glVertex2f(x, y)
    glVertex2f(x + width, y)
    glVertex2f(x + width, y + height)
    glVertex2f(x, y + height)
}

fun drawRotatedTexture(textureId: Int?, x: Float, y: Float, width: Float, height: Float, angle: Float, direction: Direction) {
    if (textureId === null) return
    glBindTexture(GL_TEXTURE_2D, textureId)
    glPushMatrix()
    glTranslatef(x + width / 2, y + height / 2, 0f)
    when (direction) {
        Direction.RIGHT -> {
            glRotatef(angle, 0f, 0f, 1f)
            glTranslatef(-width / 2, -height / 2, 0f)
        }

        Direction.LEFT -> {
            glRotatef(-angle, 0f, 0f, 1f)
            glTranslatef(-width / 2, -height / 2, 0f)
        }
    }
    drawQuadWithTexCoords(0f, 0f, width, height)
    glPopMatrix()
}

fun drawString(string: String, x: Float, y: Float, sizeMod: Int, color: Color = Color.WHITE) {
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
            glVertex2f(xMod, y - fontMetrics.descent)

            glTexCoord2f(0.0f, 1.0f)
            glVertex2f(xMod, y + heightAdjusted - fontMetrics.descent)

            glTexCoord2f(1.0f, 1.0f)
            glVertex2f(xMod + widthAdjusted, y + heightAdjusted - fontMetrics.descent)

            glTexCoord2f(1.0f, 0.0f)
            glVertex2f(xMod + widthAdjusted, y - fontMetrics.descent)

            glEnd()

            xMod += widthAdjusted
        }
    }
    glPopAttrib()
}

fun drawTexture(textureId: Int?, x: Float, y: Float, width: Float, height: Float) {

    if (textureId === null) return

    setupTextureParameters()

    glBindTexture(GL_TEXTURE_2D, textureId)
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

    drawQuadWithTexCoords(x, y, width, height)
}

fun drawQuadWithTexCoords(x: Float, y: Float, width: Float, height: Float) {
    glBegin(GL_QUADS)
    glTexCoord2f(0.0f, 1.0f)
    glVertex2f(x, y + height)
    glTexCoord2f(1.0f, 1.0f)
    glVertex2f(x + width, y + height)
    glTexCoord2f(1.0f, 0.0f)
    glVertex2f(x + width, y)
    glTexCoord2f(0.0f, 0.0f)
    glVertex2f(x, y)
    glEnd()
}

fun setupTextureParameters() {
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
}